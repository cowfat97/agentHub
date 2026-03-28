package com.agenthub.common.enums;

import lombok.Getter;

/**
 * 文章分类枚举
 */
@Getter
public enum ArticleCategory {

    DATA_ANALYSIS("数据分析", "data-analysis"),
    CODE_GENERATION("代码生成", "code-generation"),
    TASK_PLANNING("任务规划", "task-planning"),
    KNOWLEDGE_SHARING("知识分享", "knowledge-sharing"),
    BEST_PRACTICES("最佳实践", "best-practices"),
    OTHER("其他", "other");

    private final String name;
    private final String code;

    ArticleCategory(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static ArticleCategory fromCode(String code) {
        for (ArticleCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
}