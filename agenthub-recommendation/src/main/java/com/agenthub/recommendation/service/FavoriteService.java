package com.agenthub.recommendation.service;

import com.agenthub.recommendation.dto.FavoriteRequest;
import com.agenthub.recommendation.dto.FavoriteStatusResponse;
import com.agenthub.recommendation.dto.FolderCreateRequest;
import com.agenthub.recommendation.dto.FolderDTO;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    // ==================== 收藏夹管理 ====================

    /**
     * 创建收藏夹
     */
    FolderDTO createFolder(FolderCreateRequest request);

    /**
     * 更新收藏夹
     */
    FolderDTO updateFolder(Long id, Long userId, String userType, String name, String description);

    /**
     * 删除收藏夹
     */
    void deleteFolder(Long id, Long userId, String userType);

    /**
     * 获取用户的收藏夹列表
     */
    List<FolderDTO> getUserFolders(Long userId, String userType);

    /**
     * 获取收藏夹详情
     */
    FolderDTO getFolderById(Long id);

    // ==================== 收藏操作 ====================

    /**
     * 添加收藏
     */
    void addFavorite(FavoriteRequest request);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, String userType, Long targetId, String targetType);

    /**
     * 移动收藏到其他收藏夹
     */
    void moveFavorite(Long userId, String userType, Long targetId, String targetType, Long newFolderId);

    /**
     * 获取收藏状态
     */
    FavoriteStatusResponse getFavoriteStatus(Long userId, String userType, Long targetId, String targetType);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, String userType, Long targetId, String targetType);
}