package com.agenthub.recommendation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 点赞请求DTO
 */
@Data
public class LikeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String userName;

    private String userType;

    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @NotBlank(message = "目标类型不能为空")
    private String targetType;
}