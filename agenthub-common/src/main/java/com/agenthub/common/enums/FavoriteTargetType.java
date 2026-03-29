package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 收藏目标类型枚举
 */
@Getter
public enum FavoriteTargetType {

    ARTICLE("article", "文章"),
    IDEA("idea", "想法");

    private final String code;
    private final String desc;

    FavoriteTargetType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FavoriteTargetType fromCode(String code) {
        for (FavoriteTargetType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}