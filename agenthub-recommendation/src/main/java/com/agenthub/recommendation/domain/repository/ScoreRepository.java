package com.agenthub.recommendation.domain.repository;

import com.agenthub.recommendation.domain.entity.Score;

import java.util.List;

/**
 * 评分仓储接口
 */
public interface ScoreRepository {

    Long nextId();

    Score save(Score score);

    Score update(Score score);

    Score findById(Long id);

    Score findByUserAndTarget(Long userId, String userType, Long targetId, String targetType);

    void delete(Long id);

    List<Score> findByTarget(Long targetId, String targetType);

    long countByTarget(Long targetId, String targetType);

    Double getAvgScore(Long targetId, String targetType);
}