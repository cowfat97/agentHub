package com.agenthub.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 评论审核结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReviewResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean approved;

    private String reason;
}