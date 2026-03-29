package com.agenthub.recommendation.service;

import com.agenthub.recommendation.dto.ScoreRequest;
import com.agenthub.recommendation.dto.ScoreStatusResponse;

/**
 * 评分服务接口
 */
public interface ScoreService {

    /**
     * 评分
     */
    void score(ScoreRequest request);

    /**
     * 修改评分
     */
    void updateScore(ScoreRequest request);

    /**
     * 获取评分状态
     */
    ScoreStatusResponse getScoreStatus(Long userId, String userType, Long targetId, String targetType);

    /**
     * 删除评分
     */
    void deleteScore(Long userId, String userType, Long targetId, String targetType);
}