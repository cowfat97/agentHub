package com.agenthub.recommendation.service.impl;

import com.agenthub.recommendation.domain.entity.Score;
import com.agenthub.recommendation.domain.repository.ScoreRepository;
import com.agenthub.recommendation.dto.ScoreRequest;
import com.agenthub.recommendation.dto.ScoreStatusResponse;
import com.agenthub.recommendation.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 评分服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;

    @Override
    @Transactional
    public void score(ScoreRequest request) {
        // 检查是否已评分
        Score existing = scoreRepository.findByUserAndTarget(
                request.getUserId(), request.getUserType(),
                request.getTargetId(), request.getTargetType()
        );

        if (existing != null) {
            // 已评分则更新
            existing.updateScore(request.getScore());
            scoreRepository.update(existing);
            log.info("更新评分: userId={}, targetId={}, score={}", request.getUserId(), request.getTargetId(), request.getScore());
        } else {
            // 新评分
            Score score = Score.create(
                    scoreRepository.nextId(),
                    request.getUserId(),
                    request.getUserName(),
                    request.getUserType(),
                    request.getTargetId(),
                    request.getTargetType(),
                    request.getScore()
            );
            scoreRepository.save(score);
            log.info("创建评分: userId={}, targetId={}, score={}", request.getUserId(), request.getTargetId(), request.getScore());
        }
    }

    @Override
    @Transactional
    public void updateScore(ScoreRequest request) {
        Score existing = scoreRepository.findByUserAndTarget(
                request.getUserId(), request.getUserType(),
                request.getTargetId(), request.getTargetType()
        );

        if (existing == null) {
            throw new IllegalArgumentException("未找到评分记录");
        }

        existing.updateScore(request.getScore());
        scoreRepository.update(existing);
        log.info("更新评分: userId={}, targetId={}, newScore={}", request.getUserId(), request.getTargetId(), request.getScore());
    }

    @Override
    public ScoreStatusResponse getScoreStatus(Long userId, String userType, Long targetId, String targetType) {
        Score score = scoreRepository.findByUserAndTarget(userId, userType, targetId, targetType);
        Double avgScore = scoreRepository.getAvgScore(targetId, targetType);
        long count = scoreRepository.countByTarget(targetId, targetType);

        return ScoreStatusResponse.builder()
                .hasScored(score != null)
                .score(score != null ? score.getScore() : null)
                .avgScore(avgScore != null ? avgScore : 0.0)
                .scoreCount(count)
                .build();
    }

    @Override
    @Transactional
    public void deleteScore(Long userId, String userType, Long targetId, String targetType) {
        Score score = scoreRepository.findByUserAndTarget(userId, userType, targetId, targetType);
        if (score != null) {
            scoreRepository.delete(score.getId());
            log.info("删除评分: userId={}, targetId={}", userId, targetId);
        }
    }
}