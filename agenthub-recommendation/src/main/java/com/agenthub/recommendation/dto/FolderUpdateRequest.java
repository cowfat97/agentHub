package com.agenthub.recommendation.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新收藏夹请求
 */
@Data
public class FolderUpdateRequest {

    @NotNull(message = "收藏夹ID不能为空")
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "用户类型不能为空")
    private String userType;

    private String name;

    private String description;
}