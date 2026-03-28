package com.agenthub.article.service;

import com.agenthub.article.dto.ArticleCreateRequest;
import com.agenthub.article.dto.ArticleListResponse;
import com.agenthub.article.dto.ArticleQueryRequest;
import com.agenthub.article.dto.ArticleUpdateRequest;
import com.agenthub.article.dto.ArticleReviewResult;
import com.agenthub.common.dto.ArticleDTO;

/**
 * 文章服务接口
 *
 * 状态流程：
 * DRAFT -> PENDING_REVIEW -> PUBLISHED（审核通过）
 *                 -> REVIEW_FAILED（审核未通过）
 */
public interface ArticleService {

    /**
     * 创建文章（保存内容到OSS，数据库存URL）
     */
    ArticleDTO create(ArticleCreateRequest request);

    /**
     * 更新文章内容（仅草稿/审核未通过状态可更新）
     */
    ArticleDTO update(ArticleUpdateRequest request);

    /**
     * 提交审核（DRAFT -> PENDING_REVIEW）
     */
    ArticleDTO submitForReview(Long id, Long authorId);

    /**
     * 提交审核（简化版）
     */
    ArticleDTO submitForReview(Long id);

    /**
     * 执行审核（调用大模型审核，PENDING_REVIEW -> PUBLISHED/REVIEW_FAILED）
     */
    ArticleReviewResult performReview(Long id);

    /**
     * 获取审核结果
     */
    ArticleReviewResult getReviewResult(Long id);

    /**
     * 重新提交审核（REVIEW_FAILED -> DRAFT）
     */
    ArticleDTO resubmit(Long id, Long authorId);

    /**
     * 归档文章（已发布 -> 已归档）
     */
    ArticleDTO archive(Long id, Long authorId);

    /**
     * 取消归档（已归档 -> 已发布）
     */
    ArticleDTO unarchive(Long id, Long authorId);

    /**
     * 删除文章
     */
    void delete(Long id, Long authorId);

    /**
     * 删除文章（简化版）
     */
    void delete(Long id);

    /**
     * 获取文章详情（增加浏览次数）
     */
    ArticleDTO getDetail(Long id);

    /**
     * 获取文章详情（不增加浏览次数）
     */
    ArticleDTO getById(Long id);

    /**
     * 获取文章内容（从OSS获取）
     * @param id 文章ID
     * @param requesterId 请求者ID（用于权限校验）
     */
    String getArticleContent(Long id, Long requesterId);

    /**
     * 获取文章内容（简化版）
     */
    String getArticleContent(Long id);

    /**
     * 查询文章列表（分页）
     */
    ArticleListResponse list(ArticleQueryRequest request);

    /**
     * 搜索文章
     */
    ArticleListResponse search(String keyword, int pageNum, int pageSize);

    /**
     * 按分类查询
     */
    ArticleListResponse findByCategory(String category, int pageNum, int pageSize);

    /**
     * 按标签查询
     */
    ArticleListResponse findByTag(String tag, int pageNum, int pageSize);

    /**
     * 按作者查询
     */
    ArticleListResponse findByAuthor(Long authorId, int pageNum, int pageSize);

    /**
     * 点赞文章
     */
    ArticleDTO like(Long id);
}