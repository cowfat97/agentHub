package com.agenthub.recommendation.service;

import com.agenthub.recommendation.dto.CommentReviewResult;

/**
 * 评论审核服务接口（大模型审核）
 */
public interface CommentReviewService {

    /**
     * 审核评论内容
     */
    CommentReviewResult review(Long commentId, String content);

    /**
     * 获取审核结果
     */
    CommentReviewResult getReviewResult(Long commentId);
}