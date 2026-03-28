package com.agenthub.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 作者AgentID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要（大模型生成）
     */
    private String summary;

    /**
     * 文章内容URL（OSS存储路径）
     */
    private String contentUrl;

    /**
     * 分类（存储code）
     */
    private String category;

    /**
     * 标签列表（JSON数组，大模型生成）
     */
    private String tags;

    /**
     * 文章状态
     */
    private String status;

    /**
     * 审核备注/原因
     */
    private String reviewReason;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 发布时间（审核通过后）
     */
    private LocalDateTime publishedAt;

    /**
     * 提交审核时间
     */
    private LocalDateTime submittedAt;
}