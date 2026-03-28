package com.agenthub.article.domain.repository;

import com.agenthub.article.domain.entity.Article;
import com.agenthub.common.enums.ArticleCategory;
import com.agenthub.common.enums.ArticleStatus;

import java.util.List;

/**
 * 文章仓储接口
 */
public interface ArticleRepository {

    /**
     * 保存文章
     */
    Article save(Article article);

    /**
     * 更新文章
     */
    Article update(Article article);

    /**
     * 根据ID查询文章
     */
    Article findById(Long id);

    /**
     * 根据作者ID查询文章列表
     */
    List<Article> findByAuthorId(Long authorId);

    /**
     * 根据状态查询文章列表
     */
    List<Article> findByStatus(ArticleStatus status);

    /**
     * 根据分类查询文章列表
     */
    List<Article> findByCategory(ArticleCategory category);

    /**
     * 根据标签查询文章列表
     */
    List<Article> findByTag(String tag);

    /**
     * 查询所有文章
     */
    List<Article> findAll();

    /**
     * 关键词搜索文章（标题、内容、标签）
     */
    List<Article> search(String keyword);

    /**
     * 分页查询文章
     */
    List<Article> findByPage(int pageNum, int pageSize);

    /**
     * 根据状态分页查询
     */
    List<Article> findByStatusAndPage(ArticleStatus status, int pageNum, int pageSize);

    /**
     * 删除文章
     */
    void deleteById(Long id);

    /**
     * 统计文章总数
     */
    long count();

    /**
     * 根据状态统计文章数量
     */
    long countByStatus(ArticleStatus status);

    /**
     * 检查文章是否存在
     */
    boolean existsById(Long id);

    /**
     * 获取下一个ID
     */
    Long nextId();
}