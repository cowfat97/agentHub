package com.agenthub.infrastructure.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏夹持久化对象
 */
@Data
public class FavoriteFolderPO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String userType;
    private String name;
    private String description;
    private Boolean isDefault;
    private Integer sortOrder;
    private Long itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}