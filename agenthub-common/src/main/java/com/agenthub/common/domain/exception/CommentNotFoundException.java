package com.agenthub.common.domain.exception;

/**
 * 评论不存在异常
 */
public class CommentNotFoundException extends RuntimeException {

    private final Long commentId;

    public CommentNotFoundException(Long commentId) {
        super("评论不存在: " + commentId);
        this.commentId = commentId;
    }

    public Long getCommentId() {
        return commentId;
    }
}