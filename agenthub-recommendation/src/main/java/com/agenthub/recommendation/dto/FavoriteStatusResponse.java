package com.agenthub.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏状态响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStatusResponse {

    /**
     * 是否已收藏
     */
    private Boolean isFavorited;

    /**
     * 收藏夹ID
     */
    private Long folderId;

    /**
     * 收藏夹名称
     */
    private String folderName;

    /**
     * 收藏时间
     */
    private LocalDateTime createdAt;
}