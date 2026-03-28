package com.agenthub.recommendation.service.impl;

import com.agenthub.common.client.LLMClient;
import com.agenthub.common.result.ApiResponse;
import com.agenthub.recommendation.dto.CommentReviewResult;
import com.agenthub.recommendation.service.CommentReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 评论审核服务实现
 * 通过网关调用大模型进行内容审核
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentReviewServiceImpl implements CommentReviewService {

    private final LLMClient llmClient;

    // 审核结果缓存
    private final Map<Long, CommentReviewResult> reviewResultMap = new ConcurrentHashMap<>();

    @Override
    public CommentReviewResult review(Long commentId, String content) {
        log.info("审核评论: commentId={}", commentId);

        try {
            // 调用网关的大模型审核接口
            ApiResponse<LLMClient.ReviewResult> response = llmClient.reviewContent(content, "comment");

            LLMClient.ReviewResult reviewResult = response.getData();

            boolean approved = reviewResult != null && Boolean.TRUE.equals(reviewResult.getApproved());
            String reason = reviewResult != null ? reviewResult.getReason() : null;

            CommentReviewResult result = CommentReviewResult.builder()
                    .approved(approved)
                    .reason(reason)
                    .build();

            reviewResultMap.put(commentId, result);

            log.info("评论审核完成: commentId={}, approved={}", commentId, approved);
            return result;

        } catch (Exception e) {
            log.error("评论审核失败: commentId={}", commentId, e);
            // 审核失败时默认通过
            CommentReviewResult result = CommentReviewResult.builder()
                    .approved(true)
                    .reason("审核服务暂时不可用，已自动通过")
                    .build();
            reviewResultMap.put(commentId, result);
            return result;
        }
    }

    @Override
    public CommentReviewResult getReviewResult(Long commentId) {
        return reviewResultMap.get(commentId);
    }
}