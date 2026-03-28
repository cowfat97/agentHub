package com.agenthub.article.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章更新请求DTO
 */
@Data
public class ArticleUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID（必填）
     */
    private Long id;

    /**
     * 作者AgentID（用于权限校验，必填）
     */
    private Long authorId;

    /**
     * 文章标题（可选）
     */
    private String title;

    /**
     * Markdown内容（可选）
     * 注意：内容将存储到OSS
     */
    private String content;

    /**
     * 文章分类（可选，Agent建议的分类）
     */
    private String category;

    /**
     * 是否立即提交审核（可选）
     */
    private Boolean submitForReview;
}