package com.agenthub.recommendation.controller;

import com.agenthub.common.dto.LikeDTO;
import com.agenthub.common.result.ApiResponse;
import com.agenthub.recommendation.dto.LikeListResponse;
import com.agenthub.recommendation.dto.LikeRequest;
import com.agenthub.recommendation.dto.LikeStatusResponse;
import com.agenthub.recommendation.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Validated
public class LikeController {

    private final LikeService likeService;

    /**
     * 点赞
     */
    @PostMapping
    public ApiResponse<LikeDTO> like(@Valid @RequestBody LikeRequest request) {
        LikeDTO like = likeService.like(request);
        return ApiResponse.success(like);
    }

    /**
     * 取消点赞（简化版）
     */
    @DeleteMapping
    public ApiResponse<Void> unlike(@RequestParam Long targetId,
                                     @RequestParam String targetType) {
        likeService.unlike(targetId, targetType);
        return ApiResponse.success();
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/check")
    public ApiResponse<Boolean> hasLiked(@RequestParam Long userId,
                                          @RequestParam String userType,
                                          @RequestParam Long targetId,
                                          @RequestParam String targetType) {
        boolean liked = likeService.hasLiked(userId, userType, targetId, targetType);
        return ApiResponse.success(liked);
    }

    /**
     * 获取点赞状态
     */
    @GetMapping("/status")
    public ApiResponse<LikeStatusResponse> getLikeStatus(@RequestParam(required = false) Long userId,
                                                          @RequestParam(required = false) String userType,
                                                          @RequestParam Long targetId,
                                                          @RequestParam String targetType) {
        LikeStatusResponse status = likeService.getLikeStatus(userId, userType, targetId, targetType);
        return ApiResponse.success(status);
    }

    /**
     * 获取目标的点赞列表
     */
    @GetMapping("/target/{targetId}")
    public ApiResponse<LikeListResponse> getTargetLikes(
            @PathVariable Long targetId,
            @RequestParam String targetType,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        LikeListResponse response = likeService.getTargetLikes(targetId, targetType, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 统计目标点赞数
     */
    @GetMapping("/count")
    public ApiResponse<Long> countTargetLikes(@RequestParam Long targetId,
                                               @RequestParam String targetType) {
        long count = likeService.countTargetLikes(targetId, targetType);
        return ApiResponse.success(count);
    }

    /**
     * 批量检查点赞状态
     */
    @PostMapping("/batch-check")
    public ApiResponse<Map<Long, Boolean>> batchCheckLiked(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String userType,
            @RequestParam String targetType,
            @RequestBody List<Long> targetIds) {
        Map<Long, Boolean> result = likeService.batchCheckLiked(userId, userType, targetIds, targetType);
        return ApiResponse.success(result);
    }
}