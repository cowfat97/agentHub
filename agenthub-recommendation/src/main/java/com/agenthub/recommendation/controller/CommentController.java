package com.agenthub.recommendation.controller;

import com.agenthub.common.dto.CommentDTO;
import com.agenthub.common.result.ApiResponse;
import com.agenthub.recommendation.dto.CommentCreateRequest;
import com.agenthub.recommendation.dto.CommentListResponse;
import com.agenthub.recommendation.dto.CommentReviewResult;
import com.agenthub.recommendation.service.CommentReviewService;
import com.agenthub.recommendation.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评论控制器
 */
@Tag(name = "Comment", description = "评论 API")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final CommentReviewService commentReviewService;

    /**
     * 创建评论
     */
    @Operation(summary = "创建评论", description = "发表评论或回复")
    @PostMapping
    public ApiResponse<CommentDTO> create(@Valid @RequestBody CommentCreateRequest request) {
        CommentDTO comment = commentService.create(request);
        return ApiResponse.success(comment);
    }

    /**
     * 审核通过
     */
    @Operation(summary = "审核通过评论", description = "管理员审核通过评论")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CommentDTO> approve(@Parameter(description = "评论ID") @PathVariable Long id) {
        CommentDTO comment = commentService.approve(id);
        return ApiResponse.success(comment);
    }

    /**
     * 审核拒绝
     */
    @Operation(summary = "审核拒绝评论", description = "管理员审核拒绝评论")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CommentDTO> reject(@Parameter(description = "评论ID") @PathVariable Long id,
                                           @Parameter(description = "拒绝原因") @RequestParam String reason) {
        CommentDTO comment = commentService.reject(id, reason);
        return ApiResponse.success(comment);
    }

    /**
     * 执行审核（调用大模型）
     */
    @Operation(summary = "执行审核", description = "调用大模型审核评论内容")
    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ApiResponse<CommentReviewResult> performReview(@Parameter(description = "评论ID") @PathVariable Long id) {
        CommentDTO comment = commentService.getById(id);
        CommentReviewResult result = commentReviewService.review(id, comment.getContent());

        if (result.getApproved()) {
            commentService.approve(id);
        } else {
            commentService.reject(id, result.getReason());
        }

        return ApiResponse.success(result);
    }

    /**
     * 删除评论（简化版）
     */
    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "评论ID") @PathVariable Long id) {
        commentService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 获取评论详情
     */
    @Operation(summary = "获取评论详情")
    @GetMapping("/{id}")
    public ApiResponse<CommentDTO> getById(@Parameter(description = "评论ID") @PathVariable Long id) {
        CommentDTO comment = commentService.getById(id);
        return ApiResponse.success(comment);
    }

    /**
     * 获取文章评论列表
     */
    @Operation(summary = "获取文章评论列表")
    @GetMapping("/article/{articleId}")
    public ApiResponse<CommentListResponse> getArticleComments(
            @Parameter(description = "文章ID") @PathVariable Long articleId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        CommentListResponse response = commentService.getArticleComments(articleId, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 获取评论的回复列表
     */
    @Operation(summary = "获取评论回复列表")
    @GetMapping("/{rootId}/replies")
    public ApiResponse<CommentListResponse> getReplies(
            @Parameter(description = "根评论ID") @PathVariable Long rootId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        CommentListResponse response = commentService.getReplies(rootId, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 获取待审核评论列表
     */
    @Operation(summary = "获取待审核评论列表", description = "管理员查看待审核评论")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CommentListResponse> getPendingComments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        CommentListResponse response = commentService.getPendingComments(pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 点赞评论
     */
    @Operation(summary = "点赞评论")
    @PostMapping("/{id}/like")
    public ApiResponse<CommentDTO> like(@Parameter(description = "评论ID") @PathVariable Long id) {
        CommentDTO comment = commentService.like(id);
        return ApiResponse.success(comment);
    }

    /**
     * 统计文章评论数
     */
    @Operation(summary = "统计文章评论数")
    @GetMapping("/article/{articleId}/count")
    public ApiResponse<Long> countByArticleId(@Parameter(description = "文章ID") @PathVariable Long articleId) {
        long count = commentService.countByArticleId(articleId);
        return ApiResponse.success(count);
    }
}