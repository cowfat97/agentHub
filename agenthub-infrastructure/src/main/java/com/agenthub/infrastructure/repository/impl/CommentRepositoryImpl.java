package com.agenthub.infrastructure.repository.impl;

import com.agenthub.recommendation.domain.entity.Comment;
import com.agenthub.recommendation.domain.repository.CommentRepository;
import com.agenthub.common.enums.CommentStatus;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.CommentPO;
import com.agenthub.infrastructure.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论仓储实现
 */
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentMapper commentMapper;
    private final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

    // ==================== 转换方法 ====================

    private Comment toComment(CommentPO po) {
        if (po == null) {
            return null;
        }
        return Comment.builder()
                .id(po.getId())
                .articleId(po.getArticleId())
                .commenterId(po.getCommenterId())
                .commenterName(po.getCommenterName())
                .commenterType(po.getCommenterType())
                .content(po.getContent())
                .parentId(po.getParentId())
                .rootId(po.getRootId())
                .replyToId(po.getReplyToId())
                .replyToName(po.getReplyToName())
                .status(CommentStatus.fromCode(po.getStatus()))
                .reviewReason(po.getReviewReason())
                .likeCount(po.getLikeCount())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private CommentPO toCommentPO(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentPO.builder()
                .id(comment.getId())
                .articleId(comment.getArticleId())
                .commenterId(comment.getCommenterId())
                .commenterName(comment.getCommenterName())
                .commenterType(comment.getCommenterType())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .rootId(comment.getRootId())
                .replyToId(comment.getReplyToId())
                .replyToName(comment.getReplyToName())
                .status(comment.getStatus() != null ? comment.getStatus().getCode() : null)
                .reviewReason(comment.getReviewReason())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // ==================== 仓储接口实现 ====================

    @Override
    public Comment save(Comment comment) {
        CommentPO po = toCommentPO(comment);
        if (po.getCreatedAt() == null) {
            po.setCreatedAt(LocalDateTime.now());
        }
        if (po.getUpdatedAt() == null) {
            po.setUpdatedAt(LocalDateTime.now());
        }
        commentMapper.insert(po);
        return toComment(po);
    }

    @Override
    public Comment update(Comment comment) {
        CommentPO po = toCommentPO(comment);
        po.setUpdatedAt(LocalDateTime.now());
        commentMapper.update(po);
        return toComment(po);
    }

    @Override
    public Comment findById(Long id) {
        CommentPO po = commentMapper.selectById(id);
        return toComment(po);
    }

    @Override
    public List<Comment> findByArticleId(Long articleId) {
        List<CommentPO> pos = commentMapper.selectByArticleId(articleId);
        return pos.stream().map(this::toComment).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByArticleIdAndStatus(Long articleId, CommentStatus status) {
        List<CommentPO> pos = commentMapper.selectByArticleIdAndStatus(articleId, status.getCode());
        return pos.stream().map(this::toComment).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findRepliesByRootId(Long rootId) {
        List<CommentPO> pos = commentMapper.selectRepliesByRootId(rootId);
        return pos.stream().map(this::toComment).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByStatus(CommentStatus status) {
        List<CommentPO> pos = commentMapper.selectByStatus(status.getCode());
        return pos.stream().map(this::toComment).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByCommenter(Long commenterId, String commenterType) {
        List<CommentPO> pos = commentMapper.selectByCommenter(commenterId, commenterType);
        return pos.stream().map(this::toComment).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByArticleIdAndStatusWithPage(Long articleId, CommentStatus status,
                                                           int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<CommentPO> pos = commentMapper.selectByArticleIdAndStatusWithPage(
                articleId, status.getCode(), offset, pageSize);
        return pos.stream().map(this::toComment).collect(Collectors.toList());
    }

    @Override
    public long countByArticleIdAndStatus(Long articleId, CommentStatus status) {
        return commentMapper.countByArticleIdAndStatus(articleId, status.getCode());
    }

    @Override
    public long countRepliesByRootId(Long rootId) {
        return commentMapper.countRepliesByRootId(rootId);
    }

    @Override
    public void deleteById(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return commentMapper.selectById(id) != null;
    }

    @Override
    public Long nextId() {
        return idGenerator.nextId();
    }
}