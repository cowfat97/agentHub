package com.agenthub.article.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章查询请求DTO
 */
@Data
public class ArticleQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章状态筛选
     */
    private String status;

    /**
     * 分类筛选
     */
    private String category;

    /**
     * 标签筛选
     */
    private String tag;

    /**
     * 作者ID筛选
     */
    private Long authorId;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 页码（默认1）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小（默认10）
     */
    private Integer pageSize = 10;
}