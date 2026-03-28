package com.agenthub.recommendation.domain.entity;

import com.agenthub.common.enums.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论聚合根 - 领域实体
 *
 * 评论层级设计：
 * - parentId: 父评论ID，为null表示对文章的直接评论
 * - rootId: 根评论ID，用于快速查询楼中楼
 * - replyToId: 回复的目标评论ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    /**
     * 评论唯一标识
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
     * 根评论ID（用于查询楼中楼，一级评论的rootId等于自己的id）
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
    private CommentStatus status;

    /**
     * 审核备注
     */
    private String reviewReason;

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
     * 创建评论（一级评论，直接对文章评论）
     */
    public static Comment createForArticle(Long id, Long articleId, Long commenterId,
                                            String commenterName, String commenterType,
                                            String content) {
        return Comment.builder()
                .id(id)
                .articleId(articleId)
                .commenterId(commenterId)
                .commenterName(commenterName)
                .commenterType(commenterType)
                .content(content)
                .parentId(null)
                .rootId(id)
                .replyToId(null)
                .replyToName(null)
                .status(CommentStatus.PENDING)
                .likeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建回复评论（楼中楼）
     */
    public static Comment createReply(Long id, Long articleId, Long commenterId,
                                       String commenterName, String commenterType,
                                       String content, Long parentId, Long rootId,
                                       Long replyToId, String replyToName) {
        return Comment.builder()
                .id(id)
                .articleId(articleId)
                .commenterId(commenterId)
                .commenterName(commenterName)
                .commenterType(commenterType)
                .content(content)
                .parentId(parentId)
                .rootId(rootId)
                .replyToId(replyToId)
                .replyToName(replyToName)
                .status(CommentStatus.PENDING)
                .likeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 审核通过
     */
    public void approve() {
        if (this.status != CommentStatus.PENDING) {
            throw new IllegalStateException("只有待审核的评论才能审核通过");
        }
        this.status = CommentStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 审核拒绝
     */
    public void reject(String reason) {
        if (this.status != CommentStatus.PENDING) {
            throw new IllegalStateException("只有待审核的评论才能审核拒绝");
        }
        this.status = CommentStatus.REJECTED;
        this.reviewReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 删除评论
     */
    public void delete() {
        if (this.status != CommentStatus.APPROVED) {
            throw new IllegalStateException("只有已通过的评论才能删除");
        }
        this.status = CommentStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 点赞
     */
    public void like() {
        this.likeCount++;
    }

    /**
     * 是否属于指定评论者
     */
    public boolean isCommenter(Long commenterId, String commenterType) {
        return this.commenterId.equals(commenterId) && this.commenterType.equals(commenterType);
    }

    /**
     * 是否已通过审核
     */
    public boolean isApproved() {
        return this.status == CommentStatus.APPROVED;
    }

    /**
     * 是否是一级评论
     */
    public boolean isTopLevel() {
        return this.parentId == null;
    }
}