package com.agenthub.recommendation.dto;

import com.agenthub.common.dto.LikeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 点赞列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<LikeDTO> likes;

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private Boolean hasMore;
}