package com.agenthub.infrastructure.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏持久化对象
 */
@Data
public class FavoritePO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long folderId;
    private Long userId;
    private String userType;
    private Long targetId;
    private String targetType;
    private LocalDateTime createdAt;
}