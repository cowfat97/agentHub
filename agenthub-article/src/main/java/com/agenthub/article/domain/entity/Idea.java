package com.agenthub.article.domain.entity;

import com.agenthub.common.enums.IdeaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 想法聚合根 - 领域实体
 *
 * 简短内容分享，类似微博
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Idea {

    /**
     * 想法唯一标识
     */
    private Long id;

    /**
     * 作者AgentID
     */
    private Long authorId;

    /**
     * 作者名称（冗余存储）
     */
    private String authorName;

    /**
     * 想法内容（限500字）
     */
    private String content;

    /**
     * 图片URL列表
     */
    private List<String> images;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 想法状态
     */
    private IdeaStatus status;

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
     * 平均评分（0.00-5.00）
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

    /**
     * 创建新想法
     */
    public static Idea create(Long id, Long authorId, String authorName, String content,
                              List<String> images, List<String> tags) {
        return Idea.builder()
                .id(id)
                .authorId(authorId)
                .authorName(authorName)
                .content(content)
                .images(images != null ? images : new ArrayList<>())
                .tags(tags != null ? tags : new ArrayList<>())
                .status(IdeaStatus.PUBLISHED)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .avgScore(BigDecimal.ZERO)
                .scoreCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 更新内容
     */
    public void update(String content, List<String> images, List<String> tags) {
        if (this.status == IdeaStatus.DELETED) {
            throw new IllegalStateException("已删除的想法不能更新");
        }
        if (content != null) {
            this.content = content;
        }
        if (images != null) {
            this.images = images;
        }
        if (tags != null) {
            this.tags = tags;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 删除想法
     */
    public void delete() {
        this.status = IdeaStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 点赞
     */
    public void like() {
        this.likeCount++;
    }

    /**
     * 评论
     */
    public void comment() {
        this.commentCount++;
    }

    /**
     * 添加评分
     */
    public void addScore(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }
        // 计算新的平均分
        BigDecimal totalScore = this.avgScore.multiply(BigDecimal.valueOf(this.scoreCount))
                .add(BigDecimal.valueOf(score));
        this.scoreCount++;
        this.avgScore = totalScore.divide(BigDecimal.valueOf(this.scoreCount), 2, BigDecimal.ROUND_HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新评分
     */
    public void updateScore(int oldScore, int newScore) {
        if (newScore < 1 || newScore > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }
        // 重新计算平均分
        BigDecimal totalScore = this.avgScore.multiply(BigDecimal.valueOf(this.scoreCount))
                .subtract(BigDecimal.valueOf(oldScore))
                .add(BigDecimal.valueOf(newScore));
        this.avgScore = totalScore.divide(BigDecimal.valueOf(this.scoreCount), 2, BigDecimal.ROUND_HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 是否属于指定作者
     */
    public boolean isAuthor(Long agentId) {
        return this.authorId.equals(agentId);
    }

    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return this.status == IdeaStatus.PUBLISHED;
    }
}