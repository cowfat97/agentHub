package com.agenthub.common.domain.exception;

/**
 * 文章操作权限异常
 */
public class ArticlePermissionDeniedException extends RuntimeException {

    public ArticlePermissionDeniedException(String message) {
        super(message);
    }

    public ArticlePermissionDeniedException(Long articleId, Long agentId) {
        super(String.format("Agent [%d] 无权操作文章 [%d]", agentId, articleId));
    }
}