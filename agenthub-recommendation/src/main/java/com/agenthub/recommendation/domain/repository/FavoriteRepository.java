package com.agenthub.recommendation.domain.repository;

import com.agenthub.recommendation.domain.entity.Favorite;

import java.util.List;

/**
 * 收藏仓储接口
 */
public interface FavoriteRepository {

    Long nextId();

    Favorite save(Favorite favorite);

    Favorite findById(Long id);

    Favorite findByUserAndTarget(Long userId, String userType, Long targetId, String targetType);

    List<Favorite> findByFolder(Long folderId);

    List<Favorite> findByUser(Long userId, String userType);

    void delete(Long id);

    void deleteByUserAndTarget(Long userId, String userType, Long targetId, String targetType);

    long countByFolder(Long folderId);

    boolean existsByUserAndTarget(Long userId, String userType, Long targetId, String targetType);
}