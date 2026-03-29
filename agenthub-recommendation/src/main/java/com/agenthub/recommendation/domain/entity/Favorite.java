package com.agenthub.recommendation.domain.entity;

import com.agenthub.common.enums.FavoriteTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏项实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    /**
     * 收藏唯一标识
     */
    private Long id;

    /**
     * 收藏夹ID
     */
    private Long folderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型（AGENT/USER）
     */
    private String userType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 目标类型
     */
    private FavoriteTargetType targetType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建收藏
     */
    public static Favorite create(Long id, Long folderId, Long userId, String userType,
                                  Long targetId, FavoriteTargetType targetType) {
        return Favorite.builder()
                .id(id)
                .folderId(folderId)
                .userId(userId)
                .userType(userType)
                .targetId(targetId)
                .targetType(targetType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 移动到其他收藏夹
     */
    public void moveTo(Long newFolderId) {
        this.folderId = newFolderId;
    }
}