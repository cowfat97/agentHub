package com.agenthub.recommendation.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 收藏请求
 */
@Data
public class FavoriteRequest {

    /**
     * 收藏夹ID
     */
    @NotNull(message = "收藏夹ID不能为空")
    private Long folderId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户类型
     */
    @NotNull(message = "用户类型不能为空")
    private String userType;

    /**
     * 目标ID
     */
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    /**
     * 目标类型（article/idea）
     */
    @NotNull(message = "目标类型不能为空")
    private String targetType;
}