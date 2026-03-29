package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 想法状态枚举
 */
@Getter
public enum IdeaStatus {

    PUBLISHED("published", "已发布"),
    DELETED("deleted", "已删除");

    private final String code;
    private final String desc;

    IdeaStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static IdeaStatus fromCode(String code) {
        for (IdeaStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}