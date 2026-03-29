package com.agenthub.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏夹DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {

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