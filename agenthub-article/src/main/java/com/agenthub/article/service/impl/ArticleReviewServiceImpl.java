package com.agenthub.article.service.impl;

import com.agenthub.article.dto.ArticleReviewRequest;
import com.agenthub.article.dto.ArticleReviewResult;
import com.agenthub.article.service.ArticleReviewService;
import com.agenthub.common.client.LLMClient;
import com.agenthub.common.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文章审核服务实现
 * 通过网关调用大模型进行内容审核、标签提取、分类建议
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleReviewServiceImpl implements ArticleReviewService {

    private final LLMClient llmClient;

    // 异步审核结果缓存
    private final Map<Long, ArticleReviewResult> reviewResultCache = new ConcurrentHashMap<>();

    @Override
    public ArticleReviewResult review(ArticleReviewRequest request) {
        log.info("开始审核文章: articleId={}, title={}", request.getArticleId(), request.getTitle());

        long startTime = System.currentTimeMillis();
        ArticleReviewResult result;

        try {
            // 1. 内容审核
            ApiResponse<LLMClient.ReviewResult> reviewResponse = llmClient.reviewContent(
                    request.getContent(), "article");

            LLMClient.ReviewResult reviewResult = reviewResponse.getData();
            boolean approved = reviewResult != null && Boolean.TRUE.equals(reviewResult.getApproved());

            // 2. 标签提取（审核通过时）
            List<String> tags = Collections.emptyList();
            String suggestedCategory = "knowledge-sharing";

            if (approved) {
                // 提取标签
                ApiResponse<List<String>> tagsResponse = llmClient.extractTags(
                        request.getTitle(), request.getContent());
                if (tagsResponse.getData() != null) {
                    tags = tagsResponse.getData();
                }

                // 分类建议
                ApiResponse<String> categoryResponse = llmClient.suggestCategory(
                        request.getTitle(), request.getContent());
                if (categoryResponse.getData() != null) {
                    suggestedCategory = categoryResponse.getData();
                }
            }

            result = ArticleReviewResult.builder()
                    .approved(approved)
                    .reason(reviewResult != null ? reviewResult.getReason() : "审核完成")
                    .tags(tags)
                    .suggestedCategory(suggestedCategory)
                    .complianceScore(approved ? 95 : 30)
                    .qualityScore(85)
                    .build();

            reviewResultCache.put(request.getArticleId(), result);

            log.info("审核完成: articleId={}, approved={}, duration={}ms",
                    request.getArticleId(), approved, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("审核失败: articleId={}", request.getArticleId(), e);
            // 审核失败时默认通过（可根据业务需求调整）
            result = ArticleReviewResult.builder()
                    .approved(true)
                    .reason("审核服务暂时不可用，已自动通过")
                    .tags(Collections.singletonList("知识分享"))
                    .suggestedCategory("knowledge-sharing")
                    .complianceScore(80)
                    .qualityScore(80)
                    .build();
        }

        return result;
    }

    @Override
    public void submitForAsyncReview(Long articleId) {
        log.info("提交异步审核: articleId={}", articleId);
        // TODO: 发送到消息队列，异步处理审核
    }

    @Override
    public ArticleReviewResult getReviewResult(Long articleId) {
        return reviewResultCache.get(articleId);
    }

    @Override
    public boolean checkCompliance(String content) {
        try {
            ApiResponse<LLMClient.ReviewResult> response = llmClient.reviewContent(content, "article");
            LLMClient.ReviewResult result = response.getData();
            return result != null && Boolean.TRUE.equals(result.getApproved());
        } catch (Exception e) {
            log.error("合规性检查失败", e);
            return true; // 异常时默认通过
        }
    }

    @Override
    public List<String> extractTags(String content, String title) {
        try {
            ApiResponse<List<String>> response = llmClient.extractTags(title, content);
            if (response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("标签提取失败", e);
        }
        return Collections.singletonList("知识分享");
    }

    @Override
    public String suggestCategory(String content, String title) {
        try {
            ApiResponse<String> response = llmClient.suggestCategory(title, content);
            if (response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("分类建议失败", e);
        }
        return "knowledge-sharing";
    }
}