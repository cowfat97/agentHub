package com.agenthub.article.service;

import com.agenthub.article.dto.ArticleReviewRequest;
import com.agenthub.article.dto.ArticleReviewResult;

/**
 * 文章审核服务接口（调用大模型）
 */
public interface ArticleReviewService {

    /**
     * 提交文章进行审核
     * @param request 审核请求
     * @return 审核结果
     */
    ArticleReviewResult review(ArticleReviewRequest request);

    /**
     * 异步审核文章
     * @param articleId 文章ID
     */
    void submitForAsyncReview(Long articleId);

    /**
     * 获取审核结果（用于异步回调）
     * @param articleId 文章ID
     * @return 审核结果，如未完成返回null
     */
    ArticleReviewResult getReviewResult(Long articleId);

    /**
     * 内容合规性检查
     * @param content 文章内容
     * @return 是否合规
     */
    boolean checkCompliance(String content);

    /**
     * 自动提取标签
     * @param content 文章内容
     * @param title 文章标题
     * @return 标签列表
     */
    java.util.List<String> extractTags(String content, String title);

    /**
     * 自动分类建议
     * @param content 文章内容
     * @param title 文章标题
     * @return 建议的分类
     */
    String suggestCategory(String content, String title);
}