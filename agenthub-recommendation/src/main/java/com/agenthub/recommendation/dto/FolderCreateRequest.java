package com.agenthub.recommendation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建收藏夹请求
 */
@Data
public class FolderCreateRequest {

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
     * 收藏夹名称
     */
    @NotBlank(message = "收藏夹名称不能为空")
    private String name;

    /**
     * 收藏夹描述
     */
    private String description;
}