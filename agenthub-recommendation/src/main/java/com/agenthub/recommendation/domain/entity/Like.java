package com.agenthub.recommendation.domain.entity;

import com.agenthub.common.enums.LikeTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 点赞聚合根 - 领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    /**
     * 点赞唯一标识
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
    private LikeTargetType targetType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建点赞记录
     */
    public static Like create(Long id, Long userId, String userName, String userType,
                               Long targetId, LikeTargetType targetType) {
        return Like.builder()
                .id(id)
                .userId(userId)
                .userName(userName)
                .userType(userType)
                .targetId(targetId)
                .targetType(targetType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 是否属于指定用户
     */
    public boolean isOwner(Long userId, String userType) {
        return this.userId.equals(userId) && this.userType.equals(userType);
    }
}