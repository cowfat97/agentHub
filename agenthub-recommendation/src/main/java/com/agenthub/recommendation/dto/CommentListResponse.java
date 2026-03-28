package com.agenthub.recommendation.dto;

import com.agenthub.common.dto.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 评论列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CommentDTO> comments;

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private Boolean hasMore;
}