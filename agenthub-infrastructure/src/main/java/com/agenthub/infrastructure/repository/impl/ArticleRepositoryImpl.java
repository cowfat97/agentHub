package com.agenthub.infrastructure.repository.impl;

import com.agenthub.article.domain.entity.Article;
import com.agenthub.article.domain.repository.ArticleRepository;
import com.agenthub.common.enums.ArticleCategory;
import com.agenthub.common.enums.ArticleStatus;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.ArticlePO;
import com.agenthub.infrastructure.mapper.ArticleMapper;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章仓储实现
 */
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleMapper articleMapper;

    // 雪花算法ID生成器（支持分布式部署）
    private final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

    // ==================== PO 转换方法 ====================

    private Article toArticle(ArticlePO po) {
        if (po == null) {
            return null;
        }
        // 解析标签JSON
        List<String> tags = new ArrayList<>();
        if (po.getTags() != null && !po.getTags().isEmpty()) {
            tags = JSONUtil.toList(po.getTags(), String.class);
        }

        return Article.builder()
                .id(po.getId())
                .authorId(po.getAuthorId())
                .authorName(po.getAuthorName())
                .title(po.getTitle())
                .summary(po.getSummary())
                .contentUrl(po.getContentUrl())
                .category(ArticleCategory.fromCode(po.getCategory()))
                .tags(tags)
                .status(ArticleStatus.fromCode(po.getStatus()))
                .reviewReason(po.getReviewReason())
                .viewCount(po.getViewCount())
                .likeCount(po.getLikeCount())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .publishedAt(po.getPublishedAt())
                .submittedAt(po.getSubmittedAt())
                .build();
    }

    private ArticlePO toArticlePO(Article article) {
        if (article == null) {
            return null;
        }
        ArticlePO po = new ArticlePO();
        po.setId(article.getId());
        po.setAuthorId(article.getAuthorId());
        po.setAuthorName(article.getAuthorName());
        po.setTitle(article.getTitle());
        po.setSummary(article.getSummary());
        po.setContentUrl(article.getContentUrl());
        po.setCategory(article.getCategory() != null ? article.getCategory().getCode() : null);
        po.setTags(JSONUtil.toJsonStr(article.getTags()));
        po.setStatus(article.getStatus() != null ? article.getStatus().getCode() : ArticleStatus.DRAFT.getCode());
        po.setReviewReason(article.getReviewReason());
        po.setViewCount(article.getViewCount());
        po.setLikeCount(article.getLikeCount());
        po.setCreatedAt(article.getCreatedAt());
        po.setUpdatedAt(article.getUpdatedAt());
        po.setPublishedAt(article.getPublishedAt());
        po.setSubmittedAt(article.getSubmittedAt());
        return po;
    }

    // ==================== ArticleRepository 实现 ====================

    @Override
    public Article save(Article article) {
        ArticlePO po = toArticlePO(article);
        if (po.getCreatedAt() == null) {
            po.setCreatedAt(LocalDateTime.now());
        }
        if (po.getUpdatedAt() == null) {
            po.setUpdatedAt(LocalDateTime.now());
        }
        articleMapper.insert(po);
        return toArticle(po);
    }

    @Override
    public Article update(Article article) {
        ArticlePO po = toArticlePO(article);
        po.setUpdatedAt(LocalDateTime.now());
        articleMapper.update(po);
        return toArticle(po);
    }

    @Override
    public Article findById(Long id) {
        ArticlePO po = articleMapper.selectById(id);
        return toArticle(po);
    }

    @Override
    public List<Article> findByAuthorId(Long authorId) {
        List<ArticlePO> pos = articleMapper.selectByAuthorId(authorId);
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByStatus(ArticleStatus status) {
        List<ArticlePO> pos = articleMapper.selectByStatus(status.getCode());
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByCategory(ArticleCategory category) {
        List<ArticlePO> pos = articleMapper.selectByCategory(category.getCode());
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByTag(String tag) {
        List<ArticlePO> pos = articleMapper.selectByTag(tag);
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findAll() {
        List<ArticlePO> pos = articleMapper.selectAll();
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> search(String keyword) {
        List<ArticlePO> pos = articleMapper.search(keyword);
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ArticlePO> pos = articleMapper.selectByPage(offset, pageSize);
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByStatusAndPage(ArticleStatus status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ArticlePO> pos = articleMapper.selectByStatusAndPage(status.getCode(), offset, pageSize);
        return pos.stream()
                .map(this::toArticle)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public long count() {
        return articleMapper.count();
    }

    @Override
    public long countByStatus(ArticleStatus status) {
        return articleMapper.countByStatus(status.getCode());
    }

    @Override
    public boolean existsById(Long id) {
        return articleMapper.selectById(id) != null;
    }

    @Override
    public Long nextId() {
        return idGenerator.nextId();
    }
}