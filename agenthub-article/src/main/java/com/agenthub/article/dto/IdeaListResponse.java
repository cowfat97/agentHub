package com.agenthub.article.dto;

import com.agenthub.common.dto.IdeaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 想法列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaListResponse {

    /**
     * 想法列表
     */
    private List<IdeaDTO> ideas;

    /**
     * 总数
     */
    private Long total;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 是否有更多
     */
    private Boolean hasMore;
}