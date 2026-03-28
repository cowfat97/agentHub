package com.agenthub.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 点赞状态响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeStatusResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long targetId;

    private String targetType;

    private Boolean liked;

    private Long likeCount;
}