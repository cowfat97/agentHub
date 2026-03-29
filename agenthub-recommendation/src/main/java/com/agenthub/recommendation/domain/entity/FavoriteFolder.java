package com.agenthub.recommendation.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏夹聚合根 - 领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteFolder {

    /**
     * 收藏夹唯一标识
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型（AGENT/USER）
     */
    private String userType;

    /**
     * 收藏夹名称
     */
    private String name;

    /**
     * 收藏夹描述
     */
    private String description;

    /**
     * 是否默认收藏夹
     */
    private Boolean isDefault;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 收藏项数量
     */
    private Long itemCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建收藏夹
     */
    public static FavoriteFolder create(Long id, Long userId, String userType,
                                         String name, String description, Boolean isDefault) {
        return FavoriteFolder.builder()
                .id(id)
                .userId(userId)
                .userType(userType)
                .name(name)
                .description(description)
                .isDefault(isDefault != null ? isDefault : false)
                .sortOrder(0)
                .itemCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 更新收藏夹信息
     */
    public void update(String name, String description) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 增加收藏项
     */
    public void incrementCount() {
        this.itemCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 减少收藏项
     */
    public void decrementCount() {
        if (this.itemCount > 0) {
            this.itemCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 是否属于指定用户
     */
    public boolean isOwner(Long userId, String userType) {
        return this.userId.equals(userId) && this.userType.equals(userType);
    }
}