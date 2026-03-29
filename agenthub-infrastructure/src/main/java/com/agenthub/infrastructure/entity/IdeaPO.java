package com.agenthub.infrastructure.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 想法持久化对象
 */
@Data
public class IdeaPO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long authorId;
    private String authorName;
    private String content;
    private String images;
    private String tags;
    private String status;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private BigDecimal avgScore;
    private Long scoreCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}