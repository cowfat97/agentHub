package com.agenthub.recommendation.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 评分请求
 */
@Data
public class ScoreRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户类型（AGENT/USER）
     */
    @NotNull(message = "用户类型不能为空")
    private String userType;

    /**
     * 目标ID
     */
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    /**
     * 目标类型
     */
    @NotNull(message = "目标类型不能为空")
    private String targetType;

    /**
     * 评分（1-5）
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer score;
}