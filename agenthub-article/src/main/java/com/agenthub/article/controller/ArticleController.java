package com.agenthub.article.controller;

import com.agenthub.article.dto.ArticleCreateRequest;
import com.agenthub.article.dto.ArticleListResponse;
import com.agenthub.article.dto.ArticleQueryRequest;
import com.agenthub.article.dto.ArticleUpdateRequest;
import com.agenthub.article.dto.ArticleReviewResult;
import com.agenthub.article.service.ArticleService;
import com.agenthub.common.dto.ArticleDTO;
import com.agenthub.common.result.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Article", description = "文章分享 API")
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Validated
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章（内容存储到OSS）
     */
    @Operation(summary = "创建文章", description = "创建新文章，内容存储到OSS")
    @PostMapping
    public ApiResponse<ArticleDTO> create(@Valid @RequestBody ArticleCreateRequest request) {
        ArticleDTO article = articleService.create(request);
        return ApiResponse.success(article);
    }

    /**
     * 更新文章（仅草稿/审核未通过状态可更新）
     */
    @Operation(summary = "更新文章", description = "更新文章内容，仅草稿或审核未通过状态可更新")
    @PutMapping("/{id}")
    public ApiResponse<ArticleDTO> update(@Parameter(description = "文章ID") @PathVariable Long id,
                                          @RequestBody ArticleUpdateRequest request) {
        request.setId(id);
        ArticleDTO article = articleService.update(request);
        return ApiResponse.success(article);
    }

    /**
     * 删除文章（需要权限验证）
     */
    @Operation(summary = "删除文章", description = "删除文章及OSS内容")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "文章ID") @PathVariable Long id,
                                    @Parameter(description = "作者ID") @RequestParam Long authorId) {
        articleService.delete(id, authorId);
        return ApiResponse.success();
    }

    /**
     * 提交审核（DRAFT -> PENDING_REVIEW，需要权限验证）
     */
    @Operation(summary = "提交审核", description = "将文章提交审核")
    @PostMapping("/{id}/submit-review")
    public ApiResponse<ArticleDTO> submitForReview(@Parameter(description = "文章ID") @PathVariable Long id,
                                                   @Parameter(description = "作者ID") @RequestParam Long authorId) {
        ArticleDTO article = articleService.submitForReview(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 执行审核（调用大模型审核）
     * 权限控制：仅内部服务或管理员可调用
     */
    @Operation(summary = "执行审核", description = "调用大模型审核文章内容")
    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ApiResponse<ArticleReviewResult> performReview(@Parameter(description = "文章ID") @PathVariable Long id) {
        ArticleReviewResult result = articleService.performReview(id);
        return ApiResponse.success(result);
    }

    /**
     * 获取审核结果
     */
    @Operation(summary = "获取审核结果")
    @GetMapping("/{id}/review-result")
    public ApiResponse<ArticleReviewResult> getReviewResult(@Parameter(description = "文章ID") @PathVariable Long id) {
        ArticleReviewResult result = articleService.getReviewResult(id);
        return ApiResponse.success(result);
    }

    /**
     * 重新提交审核（REVIEW_FAILED -> DRAFT）
     */
    @Operation(summary = "重新提交审核", description = "审核未通过后重新提交")
    @PostMapping("/{id}/resubmit")
    public ApiResponse<ArticleDTO> resubmit(@Parameter(description = "文章ID") @PathVariable Long id,
                                            @Parameter(description = "作者ID") @RequestParam Long authorId) {
        ArticleDTO article = articleService.resubmit(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 归档文章
     */
    @Operation(summary = "归档文章")
    @PostMapping("/{id}/archive")
    public ApiResponse<ArticleDTO> archive(@Parameter(description = "文章ID") @PathVariable Long id,
                                           @Parameter(description = "作者ID") @RequestParam Long authorId) {
        ArticleDTO article = articleService.archive(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 取消归档
     */
    @Operation(summary = "取消归档")
    @PostMapping("/{id}/unarchive")
    public ApiResponse<ArticleDTO> unarchive(@Parameter(description = "文章ID") @PathVariable Long id,
                                             @Parameter(description = "作者ID") @RequestParam Long authorId) {
        ArticleDTO article = articleService.unarchive(id, authorId);
        return ApiResponse.success(article);
    }

    /**
     * 获取文章详情（增加浏览次数）
     */
    @Operation(summary = "获取文章详情", description = "获取文章详情并增加浏览次数")
    @GetMapping("/{id}")
    public ApiResponse<ArticleDTO> getDetail(@Parameter(description = "文章ID") @PathVariable Long id) {
        ArticleDTO article = articleService.getDetail(id);
        return ApiResponse.success(article);
    }

    /**
     * 获取文章内容（从OSS获取Markdown内容）
     */
    @Operation(summary = "获取文章内容", description = "从OSS获取Markdown内容")
    @GetMapping("/{id}/content")
    public ApiResponse<String> getArticleContent(@Parameter(description = "文章ID") @PathVariable Long id) {
        String content = articleService.getArticleContent(id);
        return ApiResponse.success(content);
    }

    /**
     * 文章列表查询（分页）
     */
    @Operation(summary = "文章列表查询")
    @GetMapping
    public ApiResponse<ArticleListResponse> list(ArticleQueryRequest request) {
        ArticleListResponse response = articleService.list(request);
        return ApiResponse.success(response);
    }

    /**
     * 关键词搜索
     */
    @Operation(summary = "关键词搜索文章")
    @GetMapping("/search")
    public ApiResponse<ArticleListResponse> search(@Parameter(description = "搜索关键词") @RequestParam String keyword,
                                                   @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                   @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.search(keyword, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 按分类查询
     */
    @Operation(summary = "按分类查询文章")
    @GetMapping("/category/{category}")
    public ApiResponse<ArticleListResponse> findByCategory(@Parameter(description = "分类代码") @PathVariable String category,
                                                           @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                           @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.findByCategory(category, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 按标签查询
     */
    @Operation(summary = "按标签查询文章")
    @GetMapping("/tag/{tag}")
    public ApiResponse<ArticleListResponse> findByTag(@Parameter(description = "标签") @PathVariable String tag,
                                                      @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                      @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.findByTag(tag, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 按作者查询
     */
    @Operation(summary = "按作者查询文章")
    @GetMapping("/author/{authorId}")
    public ApiResponse<ArticleListResponse> findByAuthor(@Parameter(description = "作者ID") @PathVariable Long authorId,
                                                         @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                         @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        ArticleListResponse response = articleService.findByAuthor(authorId, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 点赞文章
     */
    @Operation(summary = "点赞文章")
    @PostMapping("/{id}/like")
    public ApiResponse<ArticleDTO> like(@Parameter(description = "文章ID") @PathVariable Long id) {
        ArticleDTO article = articleService.like(id);
        return ApiResponse.success(article);
    }
}