package com.agenthub.article.domain.entity;

import com.agenthub.common.domain.event.DomainEvent;
import com.agenthub.common.domain.event.article.ArticlePublishedEvent;
import com.agenthub.common.domain.event.article.ArticleUpdatedEvent;
import com.agenthub.common.enums.ArticleCategory;
import com.agenthub.common.enums.ArticleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章聚合根 - 领域实体
 *
 * 状态流程：
 * DRAFT -> PENDING_REVIEW -> PUBLISHED（审核通过）
 *                 -> REVIEW_FAILED（审核未通过）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    /**
     * 文章唯一标识
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
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要（自动生成）
     */
    private String summary;

    /**
     * 文章内容URL（OSS存储路径）
     */
    private String contentUrl;

    /**
     * 分类（审核后可能调整）
     */
    private ArticleCategory category;

    /**
     * 标签列表（大模型自动生成）
     */
    private List<String> tags;

    /**
     * 文章状态
     */
    private ArticleStatus status;

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

    /**
     * 领域事件列表
     */
    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 获取并清空领域事件
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * 创建新文章（草稿状态）
     * 注意：tags不由用户提交，由审核时大模型生成
     */
    public static Article create(Long id, Long authorId, String authorName, String title,
                                  String contentUrl, ArticleCategory category) {
        return Article.builder()
                .id(id)
                .authorId(authorId)
                .authorName(authorName)
                .title(title)
                .summary(null) // 摘要在审核时生成
                .contentUrl(contentUrl)
                .category(category)
                .tags(new ArrayList<>()) // 标签在审核时由大模型生成
                .status(ArticleStatus.DRAFT)
                .viewCount(0L)
                .likeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 提交审核（DRAFT -> PENDING_REVIEW）
     */
    public void submitForReview() {
        if (this.status != ArticleStatus.DRAFT) {
            throw new IllegalStateException("只有草稿状态的文章才能提交审核");
        }
        this.status = ArticleStatus.PENDING_REVIEW;
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 审核通过（PENDING_REVIEW -> PUBLISHED）
     * @param tags 大模型生成的标签
     * @param summary 生成的摘要
     * @param category 可能调整的分类
     */
    public void approveReview(List<String> tags, String summary, ArticleCategory category) {
        if (this.status != ArticleStatus.PENDING_REVIEW) {
            throw new IllegalStateException("只有待审核的文章才能审核通过");
        }
        this.status = ArticleStatus.PUBLISHED;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.summary = summary;
        if (category != null) {
            this.category = category;
        }
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new ArticlePublishedEvent(
                this.id, this.authorId, this.authorName, this.title,
                this.category != null ? this.category.getCode() : null
        ));
    }

    /**
     * 审核未通过（PENDING_REVIEW -> REVIEW_FAILED）
     * @param reason 审核未通过原因
     */
    public void rejectReview(String reason) {
        if (this.status != ArticleStatus.PENDING_REVIEW) {
            throw new IllegalStateException("只有待审核的文章才能审核拒绝");
        }
        this.status = ArticleStatus.REVIEW_FAILED;
        this.reviewReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 重新提交审核（REVIEW_FAILED -> DRAFT，然后可再次提交）
     */
    public void resubmit() {
        if (this.status != ArticleStatus.REVIEW_FAILED) {
            throw new IllegalStateException("只有审核未通过的文章才能重新提交");
        }
        this.status = ArticleStatus.DRAFT;
        this.reviewReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新文章内容（仅草稿状态可更新）
     */
    public void updateContent(String title, String contentUrl, ArticleCategory category) {
        if (this.status != ArticleStatus.DRAFT && this.status != ArticleStatus.REVIEW_FAILED) {
            throw new IllegalStateException("只有草稿或审核未通过的文章才能更新");
        }
        if (title != null) {
            this.title = title;
        }
        if (contentUrl != null) {
            this.contentUrl = contentUrl;
        }
        if (category != null) {
            this.category = category;
        }
        this.updatedAt = LocalDateTime.now();
        // 如果是审核未通过状态，更新后回到草稿
        if (this.status == ArticleStatus.REVIEW_FAILED) {
            this.status = ArticleStatus.DRAFT;
            this.reviewReason = null;
        }
        this.domainEvents.add(new ArticleUpdatedEvent(this.id, this.authorId, this.title));
    }

    /**
     * 归档文章
     */
    public void archive() {
        if (this.status != ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("只有已发布的文章才能归档");
        }
        this.status = ArticleStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 取消归档（重新发布）
     */
    public void unarchive() {
        if (this.status != ArticleStatus.ARCHIVED) {
            throw new IllegalStateException("只有已归档的文章才能取消归档");
        }
        this.status = ArticleStatus.PUBLISHED;
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
     * 是否已发布
     */
    public boolean isPublished() {
        return this.status == ArticleStatus.PUBLISHED;
    }

    /**
     * 是否属于指定作者
     */
    public boolean isAuthor(Long agentId) {
        return this.authorId.equals(agentId);
    }

    /**
     * 是否可编辑
     */
    public boolean isEditable() {
        return this.status == ArticleStatus.DRAFT || this.status == ArticleStatus.REVIEW_FAILED;
    }
}