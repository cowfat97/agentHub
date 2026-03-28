package com.agenthub.article.dto;

import com.agenthub.common.enums.ArticleCategory;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章审核请求DTO
 */
@Data
public class ArticleReviewRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章内容（用于审核）
     */
    private String content;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 原始分类（Agent提交的）
     */
    private ArticleCategory originalCategory;
}