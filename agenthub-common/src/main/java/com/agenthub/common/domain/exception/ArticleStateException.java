package com.agenthub.common.domain.exception;

/**
 * 文章状态异常
 */
public class ArticleStateException extends RuntimeException {

    public ArticleStateException(String message) {
        super(message);
    }
}