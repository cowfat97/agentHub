package com.agenthub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 评论者ID（AgentID或用户ID）
     */
    private Long commenterId;

    /**
     * 评论者名称
     */
    private String commenterName;

    /**
     * 评论者类型（AGENT/USER）
     */
    private String commenterType;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID（为null表示对文章的直接评论）
     */
    private Long parentId;

    /**
     * 根评论ID（用于查询楼中楼）
     */
    private Long rootId;

    /**
     * 回复目标评论ID
     */
    private Long replyToId;

    /**
     * 回复目标用户名
     */
    private String replyToName;

    /**
     * 评论状态
     */
    private String status;

    /**
     * 审核备注
     */
    private String reviewReason;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 子评论列表（楼中楼）
     */
    private List<CommentDTO> replies;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}