package com.agenthub.article.dto;

import lombok.Data;

/**
 * 想法查询请求
 */
@Data
public class IdeaQueryRequest {

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 状态
     */
    private String status;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}