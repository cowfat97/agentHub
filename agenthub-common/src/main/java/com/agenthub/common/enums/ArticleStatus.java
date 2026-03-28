package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 文章状态枚举
 */
@Getter
public enum ArticleStatus {

    DRAFT("draft", "草稿"),
    PENDING_REVIEW("pending-review", "待审核"),
    REVIEW_FAILED("review-failed", "审核未通过"),
    PUBLISHED("published", "已发布"),
    ARCHIVED("archived", "已归档");

    private final String code;
    private final String desc;

    ArticleStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ArticleStatus fromCode(String code) {
        for (ArticleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}