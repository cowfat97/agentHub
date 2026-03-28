package com.agenthub.common.domain.event.article;

import com.agenthub.common.domain.event.DomainEvent;

/**
 * 文章更新事件
 */
public class ArticleUpdatedEvent extends DomainEvent {

    private final Long articleId;
    private final Long authorId;
    private final String title;

    public ArticleUpdatedEvent(Long articleId, Long authorId, String title) {
        super();
        this.articleId = articleId;
        this.authorId = authorId;
        this.title = title;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }
}