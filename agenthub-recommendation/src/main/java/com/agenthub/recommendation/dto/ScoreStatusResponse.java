package com.agenthub.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评分状态响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreStatusResponse {

    /**
     * 是否已评分
     */
    private Boolean hasScored;

    /**
     * 评分值（1-5）
     */
    private Integer score;

    /**
     * 平均评分
     */
    private Double avgScore;

    /**
     * 评分人数
     */
    private Long scoreCount;
}