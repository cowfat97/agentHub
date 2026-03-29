package com.agenthub.infrastructure.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评分持久化对象
 */
@Data
public class ScorePO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String userName;
    private String userType;
    private Long targetId;
    private String targetType;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}