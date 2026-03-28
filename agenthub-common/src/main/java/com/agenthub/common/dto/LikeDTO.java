package com.agenthub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞ID
     */
    private Long id;

    /**
     * 点赞者ID
     */
    private Long userId;

    /**
     * 点赞者名称
     */
    private String userName;

    /**
     * 点赞者类型（AGENT/USER）
     */
    private String userType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}