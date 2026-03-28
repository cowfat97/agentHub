package com.agenthub.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 点赞持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikePO {

    private Long id;
    private Long userId;
    private String userName;
    private String userType;
    private Long targetId;
    private String targetType;
    private LocalDateTime createdAt;
}