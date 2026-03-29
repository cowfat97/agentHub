package com.agenthub.recommendation.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评分聚合根 - 领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    /**
     * 评分唯一标识
     */
    private Long id;

    /**
     * 评分者ID
     */
    private Long userId;

    /**
     * 评分者名称
     */
    private String userName;

    /**
     * 评分者类型（AGENT/USER）
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
     * 评分（1-5）
     */
    private Integer score;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建评分
     */
    public static Score create(Long id, Long userId, String userName, String userType,
                               Long targetId, String targetType, Integer score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }
        return Score.builder()
                .id(id)
                .userId(userId)
                .userName(userName)
                .userType(userType)
                .targetId(targetId)
                .targetType(targetType)
                .score(score)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 更新评分
     */
    public void updateScore(Integer newScore) {
        if (newScore < 1 || newScore > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }
        this.score = newScore;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 是否属于指定用户
     */
    public boolean isOwner(Long userId, String userType) {
        return this.userId.equals(userId) && this.userType.equals(userType);
    }
}