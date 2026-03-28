package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 点赞目标类型枚举
 */
@Getter
public enum LikeTargetType {

    ARTICLE("article", "文章"),
    COMMENT("comment", "评论");

    private final String code;
    private final String desc;

    LikeTargetType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LikeTargetType fromCode(String code) {
        for (LikeTargetType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}