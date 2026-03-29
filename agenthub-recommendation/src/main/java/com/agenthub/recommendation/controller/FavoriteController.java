package com.agenthub.recommendation.controller;

import com.agenthub.common.result.ApiResponse;
import com.agenthub.recommendation.dto.*;
import com.agenthub.recommendation.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 收藏控制器
 */
@Tag(name = "Favorite", description = "收藏 API")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Validated
public class FavoriteController {

    private final FavoriteService favoriteService;

    // ==================== 收藏夹管理 ====================

    /**
     * 创建收藏夹
     */
    @Operation(summary = "创建收藏夹")
    @PostMapping("/folders")
    public ApiResponse<FolderDTO> createFolder(@Valid @RequestBody FolderCreateRequest request) {
        FolderDTO folder = favoriteService.createFolder(request);
        return ApiResponse.success(folder);
    }

    /**
     * 更新收藏夹
     */
    @Operation(summary = "更新收藏夹")
    @PutMapping("/folders/{id}")
    public ApiResponse<FolderDTO> updateFolder(
            @Parameter(description = "收藏夹ID") @PathVariable Long id,
            @RequestBody FolderUpdateRequest request) {
        FolderDTO folder = favoriteService.updateFolder(
                id, request.getUserId(), request.getUserType(),
                request.getName(), request.getDescription());
        return ApiResponse.success(folder);
    }

    /**
     * 删除收藏夹
     */
    @Operation(summary = "删除收藏夹")
    @DeleteMapping("/folders/{id}")
    public ApiResponse<Void> deleteFolder(
            @Parameter(description = "收藏夹ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType) {
        favoriteService.deleteFolder(id, userId, userType);
        return ApiResponse.success();
    }

    /**
     * 获取用户的收藏夹列表
     */
    @Operation(summary = "获取收藏夹列表")
    @GetMapping("/folders")
    public ApiResponse<List<FolderDTO>> getUserFolders(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType) {
        List<FolderDTO> folders = favoriteService.getUserFolders(userId, userType);
        return ApiResponse.success(folders);
    }

    /**
     * 获取收藏夹详情
     */
    @Operation(summary = "获取收藏夹详情")
    @GetMapping("/folders/{id}")
    public ApiResponse<FolderDTO> getFolder(
            @Parameter(description = "收藏夹ID") @PathVariable Long id) {
        FolderDTO folder = favoriteService.getFolderById(id);
        return ApiResponse.success(folder);
    }

    // ==================== 收藏操作 ====================

    /**
     * 添加收藏
     */
    @Operation(summary = "添加收藏")
    @PostMapping
    public ApiResponse<Void> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(request);
        return ApiResponse.success();
    }

    /**
     * 取消收藏
     */
    @Operation(summary = "取消收藏")
    @DeleteMapping
    public ApiResponse<Void> removeFavorite(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        favoriteService.removeFavorite(userId, userType, targetId, targetType);
        return ApiResponse.success();
    }

    /**
     * 移动收藏
     */
    @Operation(summary = "移动收藏到其他收藏夹")
    @PutMapping("/move")
    public ApiResponse<Void> moveFavorite(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType,
            @Parameter(description = "新收藏夹ID") @RequestParam Long newFolderId) {
        favoriteService.moveFavorite(userId, userType, targetId, targetType, newFolderId);
        return ApiResponse.success();
    }

    /**
     * 获取收藏状态
     */
    @Operation(summary = "获取收藏状态")
    @GetMapping("/status")
    public ApiResponse<FavoriteStatusResponse> getFavoriteStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "用户类型") @RequestParam String userType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        FavoriteStatusResponse response = favoriteService.getFavoriteStatus(userId, userType, targetId, targetType);
        return ApiResponse.success(response);
    }
}