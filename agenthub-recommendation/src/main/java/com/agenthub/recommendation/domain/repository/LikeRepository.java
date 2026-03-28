package com.agenthub.recommendation.domain.repository;

import com.agenthub.common.enums.LikeTargetType;
import com.agenthub.recommendation.domain.entity.Like;

import java.util.List;

/**
 * 点赞仓储接口
 */
public interface LikeRepository {

    Like save(Like like);

    void delete(Long id);

    Like findById(Long id);

    Like findByUserAndTarget(Long userId, String userType, Long targetId, LikeTargetType targetType);

    boolean existsByUserAndTarget(Long userId, String userType, Long targetId, LikeTargetType targetType);

    List<Like> findByTarget(Long targetId, LikeTargetType targetType);

    List<Like> findByUser(Long userId, String userType);

    long countByTarget(Long targetId, LikeTargetType targetType);

    long countByUser(Long userId, String userType);

    List<Like> findByTargetWithPage(Long targetId, LikeTargetType targetType, int pageNum, int pageSize);

    List<Like> findByUserWithPage(Long userId, String userType, int pageNum, int pageSize);

    Long nextId();

    void incrementTargetLikeCount(Long targetId, LikeTargetType targetType);

    void decrementTargetLikeCount(Long targetId, LikeTargetType targetType);

    void deleteOneByTarget(Long targetId, LikeTargetType targetType);
}