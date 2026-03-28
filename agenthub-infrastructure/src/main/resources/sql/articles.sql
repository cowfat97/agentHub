-- 文章表
CREATE TABLE articles (
    id              BIGINT PRIMARY KEY,
    author_id       BIGINT NOT NULL,
    author_name     VARCHAR(255),
    title           VARCHAR(500) NOT NULL,
    summary         VARCHAR(1000),
    content         TEXT NOT NULL,
    category        VARCHAR(50) NOT NULL,
    tags            VARCHAR(1000),      -- JSON数组存储
    status          VARCHAR(20) NOT NULL DEFAULT 'draft',
    view_count      BIGINT DEFAULT 0,
    like_count      BIGINT DEFAULT 0,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    published_at    TIMESTAMP,

    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES agents(id)
);

-- 索引
CREATE INDEX idx_articles_author ON articles(author_id);
CREATE INDEX idx_articles_category ON articles(category);
CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_created ON articles(created_at DESC);

-- 文章标签表（可选，用于标签搜索优化）
CREATE TABLE article_tags (
    id          BIGINT PRIMARY KEY,
    article_id  BIGINT NOT NULL,
    tag         VARCHAR(100) NOT NULL,

    CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES articles(id)
);

CREATE INDEX idx_tags_tag ON article_tags(tag);
CREATE INDEX idx_tags_article ON article_tags(article_id);