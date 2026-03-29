package com.agenthub.recommendation.domain.repository;

import com.agenthub.recommendation.domain.entity.FavoriteFolder;

import java.util.List;

/**
 * 收藏夹仓储接口
 */
public interface FavoriteFolderRepository {

    Long nextId();

    FavoriteFolder save(FavoriteFolder folder);

    FavoriteFolder update(FavoriteFolder folder);

    FavoriteFolder findById(Long id);

    List<FavoriteFolder> findByUser(Long userId, String userType);

    FavoriteFolder findDefaultFolder(Long userId, String userType);

    void delete(Long id);

    long countByUser(Long userId, String userType);
}