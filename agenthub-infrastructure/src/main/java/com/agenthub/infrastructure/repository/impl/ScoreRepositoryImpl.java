package com.agenthub.infrastructure.repository.impl;

import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.ScorePO;
import com.agenthub.infrastructure.mapper.ScoreMapper;
import com.agenthub.recommendation.domain.entity.Score;
import com.agenthub.recommendation.domain.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评分仓储实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ScoreRepositoryImpl implements ScoreRepository {

    private final ScoreMapper scoreMapper;

    @Override
    public Long nextId() {
        return SnowflakeIdGenerator.getInstance().nextId();
    }

    @Override
    public Score save(Score score) {
        ScorePO po = toPO(score);
        scoreMapper.insert(po);
        return score;
    }

    @Override
    public Score update(Score score) {
        ScorePO po = toPO(score);
        scoreMapper.update(po);
        return score;
    }

    @Override
    public Score findById(Long id) {
        ScorePO po = scoreMapper.findById(id);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public Score findByUserAndTarget(Long userId, String userType, Long targetId, String targetType) {
        ScorePO po = scoreMapper.findByUserAndTarget(userId, userType, targetId, targetType);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public void delete(Long id) {
        scoreMapper.delete(id);
    }

    @Override
    public List<Score> findByTarget(Long targetId, String targetType) {
        return scoreMapper.findByTarget(targetId, targetType).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countByTarget(Long targetId, String targetType) {
        return scoreMapper.countByTarget(targetId, targetType);
    }

    @Override
    public Double getAvgScore(Long targetId, String targetType) {
        return scoreMapper.getAvgScore(targetId, targetType);
    }

    private ScorePO toPO(Score score) {
        ScorePO po = new ScorePO();
        po.setId(score.getId());
        po.setUserId(score.getUserId());
        po.setUserName(score.getUserName());
        po.setUserType(score.getUserType());
        po.setTargetId(score.getTargetId());
        po.setTargetType(score.getTargetType());
        po.setScore(score.getScore());
        po.setCreatedAt(score.getCreatedAt());
        po.setUpdatedAt(score.getUpdatedAt());
        return po;
    }

    private Score toEntity(ScorePO po) {
        return Score.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .userName(po.getUserName())
                .userType(po.getUserType())
                .targetId(po.getTargetId())
                .targetType(po.getTargetType())
                .score(po.getScore())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
}