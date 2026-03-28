package com.agenthub.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPO {

    private Long id;
    private Long articleId;
    private Long commenterId;
    private String commenterName;
    private String commenterType;
    private String content;
    private Long parentId;
    private Long rootId;
    private Long replyToId;
    private String replyToName;
    private String status;
    private String reviewReason;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}