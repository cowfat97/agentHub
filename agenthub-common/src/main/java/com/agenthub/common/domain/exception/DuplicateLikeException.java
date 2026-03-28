package com.agenthub.common.domain.exception;

/**
 * 重复点赞异常
 */
public class DuplicateLikeException extends RuntimeException {

    public DuplicateLikeException(Long userId, Long targetId, String targetType) {
        super(String.format("用户 %d 已对 %s %d 点赞过", userId, targetType, targetId));
    }
}