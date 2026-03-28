package com.agenthub.article.dto;

import com.agenthub.common.dto.ArticleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文章列表响应DTO（分页）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章列表
     */
    private List<ArticleDTO> articles;

    /**
     * 总数量
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 是否有更多
     */
    private Boolean hasMore;
}