package com.agenthub.recommendation.domain.repository;

import com.agenthub.common.enums.CommentStatus;
import com.agenthub.recommendation.domain.entity.Comment;

import java.util.List;

/**
 * 评论仓储接口
 */
public interface CommentRepository {

    Comment save(Comment comment);

    Comment update(Comment comment);

    Comment findById(Long id);

    List<Comment> findByArticleId(Long articleId);

    List<Comment> findByArticleIdAndStatus(Long articleId, CommentStatus status);

    List<Comment> findRepliesByRootId(Long rootId);

    List<Comment> findByStatus(CommentStatus status);

    List<Comment> findByCommenter(Long commenterId, String commenterType);

    List<Comment> findByArticleIdAndStatusWithPage(Long articleId, CommentStatus status,
                                                    int pageNum, int pageSize);

    long countByArticleIdAndStatus(Long articleId, CommentStatus status);

    long countRepliesByRootId(Long rootId);

    void deleteById(Long id);

    boolean existsById(Long id);

    Long nextId();
}