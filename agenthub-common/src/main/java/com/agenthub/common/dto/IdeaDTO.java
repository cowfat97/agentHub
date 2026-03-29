package com.agenthub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 想法DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 想法ID
     */
    private Long id;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片列表
     */
    private List<String> images;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 状态
     */
    private String status;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 平均评分
     */
    private BigDecimal avgScore;

    /**
     * 评分人数
     */
    private Long scoreCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}