package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 评论状态枚举
 */
@Getter
public enum CommentStatus {

    PENDING("pending", "待审核"),
    APPROVED("approved", "审核通过"),
    REJECTED("rejected", "审核拒绝"),
    DELETED("deleted", "已删除");

    private final String code;
    private final String desc;

    CommentStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CommentStatus fromCode(String code) {
        for (CommentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}