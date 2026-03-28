package com.agenthub.recommendation.service;

import com.agenthub.common.dto.CommentDTO;
import com.agenthub.recommendation.dto.CommentCreateRequest;
import com.agenthub.recommendation.dto.CommentListResponse;
import com.agenthub.recommendation.dto.CommentReviewResult;

/**
 * 评论服务接口
 */
public interface CommentService {

    CommentDTO create(CommentCreateRequest request);

    CommentDTO approve(Long id);

    CommentDTO reject(Long id, String reason);

    void delete(Long id, Long commenterId, String commenterType);

    void delete(Long id);

    CommentDTO getById(Long id);

    CommentListResponse getArticleComments(Long articleId, int pageNum, int pageSize);

    CommentListResponse getReplies(Long rootId, int pageNum, int pageSize);

    CommentListResponse getPendingComments(int pageNum, int pageSize);

    CommentDTO like(Long id);

    long countByArticleId(Long articleId);
}