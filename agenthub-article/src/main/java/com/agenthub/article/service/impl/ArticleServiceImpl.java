package com.agenthub.article.service.impl;

import com.agenthub.article.domain.entity.Article;
import com.agenthub.article.domain.repository.ArticleRepository;
import com.agenthub.article.dto.*;
import com.agenthub.article.service.ArticleReviewService;
import com.agenthub.article.service.ArticleService;
import com.agenthub.common.domain.event.DomainEventPublisher;
import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.domain.exception.ArticleNotFoundException;
import com.agenthub.common.domain.exception.ArticlePermissionDeniedException;
import com.agenthub.common.domain.exception.ArticleStateException;
import com.agenthub.common.dto.ArticleDTO;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.enums.ArticleCategory;
import com.agenthub.common.enums.ArticleStatus;
import com.agenthub.common.service.FileStorageService;
import com.agenthub.common.utils.XssUtils;
import com.agenthub.discovery.service.AgentDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现
 *
 * 流程说明：
 * 1. 创建文章：内容存OSS -> 数据库存URL -> 草稿状态
 * 2. 提交审核：草稿 -> 待审核
 * 3. 大模型审核：待审核 -> 已发布/审核未通过（自动提取标签）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final AgentDiscoveryService agentDiscoveryService;
    private final ArticleReviewService reviewService;
    private final FileStorageService fileStorageService;
    private final DomainEventPublisher eventPublisher;

    @Value("${agenthub.oss.article-bucket:agenthub-articles}")
    private String articleBucket;

    // ==================== DTO 转换方法 ====================

    private ArticleDTO toDTO(Article article) {
        if (article == null) {
            return null;
        }
        return ArticleDTO.builder()
                .id(article.getId())
                .authorId(article.getAuthorId())
                .authorName(article.getAuthorName())
                .title(article.getTitle())
                .summary(article.getSummary())
                .contentUrl(article.getContentUrl())
                .category(article.getCategory() != null ? article.getCategory().getCode() : null)
                .tags(article.getTags())
                .status(article.getStatus() != null ? article.getStatus().getCode() : null)
                .reviewReason(article.getReviewReason())
                .viewCount(article.getViewCount())
                .likeCount(article.getLikeCount())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .publishedAt(article.getPublishedAt())
                .submittedAt(article.getSubmittedAt())
                .build();
    }

    // ==================== 业务方法实现 ====================

    @Override
    @Transactional
    public ArticleDTO create(ArticleCreateRequest request) {
        // 验证作者Agent存在
        AgentDTO author = agentDiscoveryService.findById(request.getAuthorId());
        if (author == null) {
            throw new AgentNotFoundException(request.getAuthorId());
        }

        // XSS过滤标题和内容
        String sanitizedTitle = XssUtils.sanitizeTitle(request.getTitle());
        String sanitizedContent = XssUtils.sanitizeArticle(request.getContent());

        // 生成文章ID
        Long articleId = articleRepository.nextId();

        // 将内容上传到OSS
        String ossPath = fileStorageService.generateArticlePath(articleId);
        String contentUrl = fileStorageService.uploadText(articleBucket, ossPath, sanitizedContent);

        // 创建文章实体（草稿状态）
        ArticleCategory category = ArticleCategory.fromCode(request.getCategory());
        Article article = Article.create(
                articleId,
                request.getAuthorId(),
                author.getName(),
                sanitizedTitle,
                contentUrl,
                category
        );

        // 保存到数据库
        Article saved = articleRepository.save(article);
        log.info("创建文章成功: id={}, authorId={}, contentUrl={}", articleId, request.getAuthorId(), contentUrl);

        // 如果请求立即提交审核
        if (request.getSubmitForReview() == null || request.getSubmitForReview()) {
            saved = articleRepository.findById(articleId);
            saved.submitForReview();
            articleRepository.update(saved);
            log.info("文章已提交审核: id={}", articleId);
        }

        return toDTO(saved);
    }

    @Override
    @Transactional
    public ArticleDTO update(ArticleUpdateRequest request) {
        Article article = articleRepository.findById(request.getId());
        if (article == null) {
            throw new ArticleNotFoundException(request.getId());
        }

        // 权限校验：只有作者可以更新
        if (!article.isAuthor(request.getAuthorId())) {
            throw new ArticlePermissionDeniedException(request.getId(), request.getAuthorId());
        }

        // 检查是否可编辑
        if (!article.isEditable()) {
            throw new ArticleStateException("当前状态的文章不能编辑，当前状态: " + article.getStatus());
        }

        // XSS过滤标题
        String sanitizedTitle = request.getTitle() != null
                ? XssUtils.sanitizeTitle(request.getTitle()) : null;

        String contentUrl = article.getContentUrl();
        // 如果有新内容，上传到OSS
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            // XSS过滤内容
            String sanitizedContent = XssUtils.sanitizeArticle(request.getContent());
            String ossPath = fileStorageService.generateArticlePath(article.getId());
            contentUrl = fileStorageService.uploadText(articleBucket, ossPath, sanitizedContent);
        }

        // 更新文章
        ArticleCategory category = ArticleCategory.fromCode(request.getCategory());
        article.updateContent(sanitizedTitle, contentUrl, category);

        // 保存更新
        Article updated = articleRepository.update(article);
        log.info("更新文章成功: id={}", request.getId());

        // 如果请求立即提交审核
        if (request.getSubmitForReview() != null && request.getSubmitForReview()) {
            updated = articleRepository.findById(request.getId());
            updated.submitForReview();
            articleRepository.update(updated);
            log.info("文章已重新提交审核: id={}", request.getId());
        }

        return toDTO(updated);
    }

    @Override
    @Transactional
    public ArticleDTO submitForReview(Long id, Long authorId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 权限校验
        if (!article.isAuthor(authorId)) {
            throw new ArticlePermissionDeniedException(id, authorId);
        }

        // 提交审核
        article.submitForReview();
        Article updated = articleRepository.update(article);

        log.info("文章已提交审核: id={}, authorId={}", id, authorId);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public ArticleDTO submitForReview(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 提交审核（简化版，无权限校验）
        article.submitForReview();
        Article updated = articleRepository.update(article);

        log.info("文章已提交审核（简化版）: id={}", id);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public ArticleReviewResult performReview(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 状态校验
        if (article.getStatus() != ArticleStatus.PENDING_REVIEW) {
            throw new ArticleStateException("只能审核待审核状态的文章，当前状态: " + article.getStatus());
        }

        // 从OSS获取内容进行审核
        String content = fileStorageService.getTextContent(articleBucket,
                fileStorageService.generateArticlePath(id));

        // 构建审核请求
        ArticleReviewRequest reviewRequest = new ArticleReviewRequest();
        reviewRequest.setArticleId(id);
        reviewRequest.setContent(content);
        reviewRequest.setTitle(article.getTitle());
        reviewRequest.setOriginalCategory(article.getCategory());

        // 调用审核服务
        ArticleReviewResult result = reviewService.review(reviewRequest);

        // 根据审核结果更新文章状态
        if (result.getApproved()) {
            // 审核通过
            ArticleCategory category = ArticleCategory.fromCode(result.getSuggestedCategory());
            article.approveReview(result.getTags(), generateSummary(content), category);
            log.info("文章审核通过: id={}, tags={}", id, result.getTags());
        } else {
            // 审核未通过
            article.rejectReview(result.getReason());
            log.warn("文章审核未通过: id={}, reason={}", id, result.getReason());
        }

        articleRepository.update(article);

        // 发布领域事件
        eventPublisher.publishAll(article.pullDomainEvents());

        return result;
    }

    @Override
    public ArticleReviewResult getReviewResult(Long id) {
        return reviewService.getReviewResult(id);
    }

    @Override
    @Transactional
    public ArticleDTO resubmit(Long id, Long authorId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 权限校验
        if (!article.isAuthor(authorId)) {
            throw new ArticlePermissionDeniedException(id, authorId);
        }

        // 重新提交
        article.resubmit();
        Article updated = articleRepository.update(article);

        log.info("文章已重新提交: id={}, authorId={}", id, authorId);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public ArticleDTO archive(Long id, Long authorId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 权限校验
        if (!article.isAuthor(authorId)) {
            throw new ArticlePermissionDeniedException(id, authorId);
        }

        // 归档文章
        article.archive();
        Article updated = articleRepository.update(article);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public ArticleDTO unarchive(Long id, Long authorId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 权限校验
        if (!article.isAuthor(authorId)) {
            throw new ArticlePermissionDeniedException(id, authorId);
        }

        // 取消归档
        article.unarchive();
        Article updated = articleRepository.update(article);

        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, Long authorId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 权限校验
        if (!article.isAuthor(authorId)) {
            throw new ArticlePermissionDeniedException(id, authorId);
        }

        // 删除OSS文件
        String ossPath = fileStorageService.generateArticlePath(id);
        fileStorageService.delete(articleBucket, ossPath);

        // 删除数据库记录
        articleRepository.deleteById(id);
        log.info("删除文章成功: id={}, authorId={}", id, authorId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 删除OSS文件
        String ossPath = fileStorageService.generateArticlePath(id);
        fileStorageService.delete(articleBucket, ossPath);

        // 删除数据库记录（简化版，无权限校验）
        articleRepository.deleteById(id);
        log.info("删除文章成功（简化版）: id={}", id);
    }

    @Override
    @Transactional
    public ArticleDTO getDetail(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 增加浏览次数（仅对已发布文章）
        if (article.isPublished()) {
            article.incrementViewCount();
            articleRepository.update(article);
        }

        return toDTO(article);
    }

    @Override
    public ArticleDTO getById(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }
        return toDTO(article);
    }

    @Override
    public String getArticleContent(Long id, Long requesterId) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 权限校验：已发布文章任何人可读，非发布状态仅作者可读
        if (!article.isPublished()) {
            if (requesterId == null || !article.isAuthor(requesterId)) {
                throw new ArticlePermissionDeniedException(id, requesterId);
            }
        }

        // 从OSS获取内容
        String ossPath = fileStorageService.generateArticlePath(id);
        return fileStorageService.getTextContent(articleBucket, ossPath);
    }

    @Override
    public String getArticleContent(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 从OSS获取内容（简化版，无权限校验）
        String ossPath = fileStorageService.generateArticlePath(id);
        return fileStorageService.getTextContent(articleBucket, ossPath);
    }

    @Override
    public ArticleListResponse list(ArticleQueryRequest request) {
        List<Article> articles;
        long total;

        // 根据查询条件筛选
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            // 关键词搜索
            articles = articleRepository.search(request.getKeyword());
            total = articles.size();
            // 分页处理
            int start = (request.getPageNum() - 1) * request.getPageSize();
            int end = Math.min(start + request.getPageSize(), articles.size());
            articles = articles.subList(start, end);
        } else if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            // 按状态查询
            ArticleStatus status = ArticleStatus.fromCode(request.getStatus());
            articles = articleRepository.findByStatusAndPage(status, request.getPageNum(), request.getPageSize());
            total = articleRepository.countByStatus(status);
        } else {
            // 默认查询所有
            articles = articleRepository.findByPage(request.getPageNum(), request.getPageSize());
            total = articleRepository.count();
        }

        List<ArticleDTO> articleDTOs = articles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ArticleListResponse.builder()
                .articles(articleDTOs)
                .total(total)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .hasMore((long) request.getPageNum() * request.getPageSize() < total)
                .build();
    }

    @Override
    public ArticleListResponse search(String keyword, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.search(keyword);
        long total = articles.size();

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, articles.size());
        List<Article> pagedArticles = articles.subList(start, end);

        List<ArticleDTO> articleDTOs = pagedArticles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ArticleListResponse.builder()
                .articles(articleDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public ArticleListResponse findByCategory(String category, int pageNum, int pageSize) {
        ArticleCategory articleCategory = ArticleCategory.fromCode(category);
        List<Article> articles = articleRepository.findByCategory(articleCategory);
        long total = articles.size();

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, articles.size());
        List<Article> pagedArticles = articles.subList(start, end);

        List<ArticleDTO> articleDTOs = pagedArticles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ArticleListResponse.builder()
                .articles(articleDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public ArticleListResponse findByTag(String tag, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findByTag(tag);
        long total = articles.size();

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, articles.size());
        List<Article> pagedArticles = articles.subList(start, end);

        List<ArticleDTO> articleDTOs = pagedArticles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ArticleListResponse.builder()
                .articles(articleDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public ArticleListResponse findByAuthor(Long authorId, int pageNum, int pageSize) {
        List<Article> articles = articleRepository.findByAuthorId(authorId);
        long total = articles.size();

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, articles.size());
        List<Article> pagedArticles = articles.subList(start, end);

        List<ArticleDTO> articleDTOs = pagedArticles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ArticleListResponse.builder()
                .articles(articleDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    @Transactional
    public ArticleDTO like(Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }

        // 点赞（仅对已发布文章）
        if (article.isPublished()) {
            article.like();
            articleRepository.update(article);
        }

        return toDTO(article);
    }

    /**
     * 生成文章摘要
     */
    private String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        // 移除Markdown标记，取前200字符
        String plainText = content.replaceAll("[#*`\\[\\]]", "").trim();
        return plainText.length() > 200 ? plainText.substring(0, 200) + "..." : plainText;
    }
}