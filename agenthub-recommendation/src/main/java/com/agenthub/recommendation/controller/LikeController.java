package com.agenthub.recommendation.controller;

import com.agenthub.common.dto.LikeDTO;
import com.agenthub.common.result.ApiResponse;
import com.agenthub.recommendation.dto.LikeListResponse;
import com.agenthub.recommendation.dto.LikeRequest;
import com.agenthub.recommendation.dto.LikeStatusResponse;
import com.agenthub.recommendation.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 点赞控制器
 */
@Tag(name = "Like", description = "点赞 API")
@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Validated
public class LikeController {

    private final LikeService likeService;

    /**
     * 点赞
     */
    @Operation(summary = "点赞", description = "对文章或评论进行点赞")
    @PostMapping
    public ApiResponse<LikeDTO> like(@Valid @RequestBody LikeRequest request) {
        LikeDTO like = likeService.like(request);
        return ApiResponse.success(like);
    }

    /**
     * 取消点赞（简化版）
     */
    @Operation(summary = "取消点赞")
    @DeleteMapping
    public ApiResponse<Void> unlike(@Parameter(description = "目标ID") @RequestParam Long targetId,
                                     @Parameter(description = "目标类型: article/comment") @RequestParam String targetType) {
        likeService.unlike(targetId, targetType);
        return ApiResponse.success();
    }

    /**
     * 检查是否已点赞
     */
    @Operation(summary = "检查是否已点赞")
    @GetMapping("/check")
    public ApiResponse<Boolean> hasLiked(@Parameter(description = "用户ID") @RequestParam Long userId,
                                          @Parameter(description = "用户类型: agent/user") @RequestParam String userType,
                                          @Parameter(description = "目标ID") @RequestParam Long targetId,
                                          @Parameter(description = "目标类型: article/comment") @RequestParam String targetType) {
        boolean liked = likeService.hasLiked(userId, userType, targetId, targetType);
        return ApiResponse.success(liked);
    }

    /**
     * 获取点赞状态
     */
    @Operation(summary = "获取点赞状态")
    @GetMapping("/status")
    public ApiResponse<LikeStatusResponse> getLikeStatus(@Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
                                                          @Parameter(description = "用户类型") @RequestParam(required = false) String userType,
                                                          @Parameter(description = "目标ID") @RequestParam Long targetId,
                                                          @Parameter(description = "目标类型") @RequestParam String targetType) {
        LikeStatusResponse status = likeService.getLikeStatus(userId, userType, targetId, targetType);
        return ApiResponse.success(status);
    }

    /**
     * 获取目标的点赞列表
     */
    @Operation(summary = "获取目标的点赞列表")
    @GetMapping("/target/{targetId}")
    public ApiResponse<LikeListResponse> getTargetLikes(
            @Parameter(description = "目标ID") @PathVariable Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        LikeListResponse response = likeService.getTargetLikes(targetId, targetType, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 统计目标点赞数
     */
    @Operation(summary = "统计目标点赞数")
    @GetMapping("/count")
    public ApiResponse<Long> countTargetLikes(@Parameter(description = "目标ID") @RequestParam Long targetId,
                                               @Parameter(description = "目标类型") @RequestParam String targetType) {
        long count = likeService.countTargetLikes(targetId, targetType);
        return ApiResponse.success(count);
    }

    /**
     * 批量检查点赞状态
     */
    @Operation(summary = "批量检查点赞状态")
    @PostMapping("/batch-check")
    public ApiResponse<Map<Long, Boolean>> batchCheckLiked(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "用户类型") @RequestParam(required = false) String userType,
            @Parameter(description = "目标类型") @RequestParam String targetType,
            @RequestBody List<Long> targetIds) {
        Map<Long, Boolean> result = likeService.batchCheckLiked(userId, userType, targetIds, targetType);
        return ApiResponse.success(result);
    }
}