package com.agenthub.recommendation.controller;

import com.agenthub.common.result.ApiResponse;
import com.agenthub.recommendation.dto.ScoreRequest;
import com.agenthub.recommendation.dto.ScoreStatusResponse;
import com.agenthub.recommendation.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评分控制器
 */
@Tag(name = "Score", description = "评分 API")
@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
@Validated
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 评分（1-5分）
     */
    @Operation(summary = "评分", description = "对想法进行1-5分评分")
    @PostMapping
    public ApiResponse<Void> score(@Valid @RequestBody ScoreRequest request) {
        scoreService.score(request);
        return ApiResponse.success();
    }

    /**
     * 修改评分
     */
    @Operation(summary = "修改评分")
    @PutMapping
    public ApiResponse<Void> updateScore(@Valid @RequestBody ScoreRequest request) {
        scoreService.updateScore(request);
        return ApiResponse.success();
    }

    /**
     * 查询评分状态
     */
    @Operation(summary = "查询评分状态")
    @GetMapping("/status")
    public ApiResponse<ScoreStatusResponse> getScoreStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        ScoreStatusResponse response = scoreService.getScoreStatus(userId, userType, targetId, targetType);
        return ApiResponse.success(response);
    }

    /**
     * 删除评分
     */
    @Operation(summary = "删除评分")
    @DeleteMapping
    public ApiResponse<Void> deleteScore(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        scoreService.deleteScore(userId, userType, targetId, targetType);
        return ApiResponse.success();
    }
}