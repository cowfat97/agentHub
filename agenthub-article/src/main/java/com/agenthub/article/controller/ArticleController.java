package com.agenthub.article.controller;

import com.agenthub.article.dto.ArticleCreateRequest;
import com.agenthub.article.dto.ArticleListResponse;
import com.agenthub.article.dto.ArticleQueryRequest;
import com.agenthub.article.dto.ArticleUpdateRequest;
import com.agenthub.article.dto.ArticleReviewResult;
import com.agenthub.article.service.ArticleService;
import com.agenthub.common.dto.ArticleDTO;
import com.agenthub.common.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 文章控制器
 *
 * 状态流程：
 * DRAFT -> PENDING_REVIEW -> PUBLISHED（审核通过）
 *                 -> REVIEW_FAILED（审核未通过）
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Validated
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章（内容存储到OSS）
     */
    @PostMapping
    public ApiResponse<ArticleDTO> create(@Valid @RequestBody ArticleCreateRequest request) {
        ArticleDTO article = articleService.create(request);
        return ApiResponse.success(article);
    }

    /**
     * 更新文章（仅草稿/审核未通过状态可更新）
     */
    @PutMapping("/{id}")
    public ApiResponse<ArticleDTO> update(@PathVariable Long id,
                                          @RequestBody ArticleUpdateRequest request) {
        request.setId(id);
        ArticleDTO article = articleService.update(request);
        return ApiResponse.success(article);
    }

    /**
     * 删除文章（需要权限验证）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestParam Long authorId) {
        articleService.delete(id, authorId);
        return ApiResponse.success();
    }

    /**
     * 提交审核（DRAFT -> PENDING_REVIEW，需要权限验证）
     */
    @PostMapping("/{id}/submit-review")
    public ApiResponse<ArticleDTO> submitForReview(@PathVariable Long id,
                                                   @RequestParam Long authorId) {
        ArticleDTO article = articleService.submitForReview(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 执行审核（调用大模型审核）
     * 权限控制：仅内部服务或管理员可调用
     */
    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ApiResponse<ArticleReviewResult> performReview(@PathVariable Long id) {
        ArticleReviewResult result = articleService.performReview(id);
        return ApiResponse.success(result);
    }

    /**
     * 获取审核结果
     */
    @GetMapping("/{id}/review-result")
    public ApiResponse<ArticleReviewResult> getReviewResult(@PathVariable Long id) {
        ArticleReviewResult result = articleService.getReviewResult(id);
        return ApiResponse.success(result);
    }

    /**
     * 重新提交审核（REVIEW_FAILED -> DRAFT）
     */
    @PostMapping("/{id}/resubmit")
    public ApiResponse<ArticleDTO> resubmit(@PathVariable Long id,
                                            @RequestParam Long authorId) {
        ArticleDTO article = articleService.resubmit(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 归档文章
     */
    @PostMapping("/{id}/archive")
    public ApiResponse<ArticleDTO> archive(@PathVariable Long id,
                                           @RequestParam Long authorId) {
        ArticleDTO article = articleService.archive(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 取消归档
     */
    @PostMapping("/{id}/unarchive")
    public ApiResponse<ArticleDTO> unarchive(@PathVariable Long id,
                                             @RequestParam Long authorId) {
        ArticleDTO article = articleService.unarchive(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 获取文章详情（增加浏览次数）
     */
    @GetMapping("/{id}")
    public ApiResponse<ArticleDTO> getDetail(@PathVariable Long id) {
        ArticleDTO article = articleService.getDetail(id);
        return ApiResponse.success(article);
    }

    /**
     * 获取文章内容（从OSS获取Markdown内容）
     */
    @GetMapping("/{id}/content")
    public ApiResponse<String> getArticleContent(@PathVariable Long id) {
        String content = articleService.getArticleContent(id);
        return ApiResponse.success(content);
    }

    /**
     * 文章列表查询（分页）
     */
    @GetMapping
    public ApiResponse<ArticleListResponse> list(ArticleQueryRequest request) {
        ArticleListResponse response = articleService.list(request);
        return ApiResponse.success(response);
    }

    /**
     * 关键词搜索
     */
    @GetMapping("/search")
    public ApiResponse<ArticleListResponse> search(@RequestParam String keyword,
                                                   @RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.search(keyword, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 按分类查询
     */
    @GetMapping("/category/{category}")
    public ApiResponse<ArticleListResponse> findByCategory(@PathVariable String category,
                                                           @RequestParam(defaultValue = "1") int pageNum,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.findByCategory(category, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 按标签查询
     */
    @GetMapping("/tag/{tag}")
    public ApiResponse<ArticleListResponse> findByTag(@PathVariable String tag,
                                                      @RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.findByTag(tag, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 按作者查询
     */
    @GetMapping("/author/{authorId}")
    public ApiResponse<ArticleListResponse> findByAuthor(@PathVariable Long authorId,
                                                         @RequestParam(defaultValue = "1") int pageNum,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.findByAuthor(authorId, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 点赞文章
     */
    @PostMapping("/{id}/like")
    public ApiResponse<ArticleDTO> like(@PathVariable Long id) {
        ArticleDTO article = articleService.like(id);
        return ApiResponse.success(article);
    }
}