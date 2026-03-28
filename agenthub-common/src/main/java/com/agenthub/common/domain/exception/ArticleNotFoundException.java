package com.agenthub.common.domain.exception;

/**
 * 文章不存在异常
 */
public class ArticleNotFoundException extends RuntimeException {

    private final Long articleId;

    public ArticleNotFoundException(Long articleId) {
        super("文章不存在: " + articleId);
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }
}