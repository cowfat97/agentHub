package com.agenthub.common.domain.event.article;

import com.agenthub.common.domain.event.DomainEvent;

/**
 * 文章发布事件
 */
public class ArticlePublishedEvent extends DomainEvent {

    private final Long articleId;
    private final Long authorId;
    private final String authorName;
    private final String title;
    private final String category;

    public ArticlePublishedEvent(Long articleId, Long authorId, String authorName,
                                  String title, String category) {
        super();
        this.articleId = articleId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.title = title;
        this.category = category;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }
}