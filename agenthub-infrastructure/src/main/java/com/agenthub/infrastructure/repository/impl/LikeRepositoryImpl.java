package com.agenthub.infrastructure.repository.impl;

import com.agenthub.common.enums.LikeTargetType;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.recommendation.domain.entity.Like;
import com.agenthub.recommendation.domain.repository.LikeRepository;
import com.agenthub.infrastructure.entity.LikePO;
import com.agenthub.infrastructure.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点赞仓储实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeMapper likeMapper;
    private final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

    // ==================== 转换方法 ====================

    private Like toLike(LikePO po) {
        if (po == null) {
            return null;
        }
        return Like.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .userName(po.getUserName())
                .userType(po.getUserType())
                .targetId(po.getTargetId())
                .targetType(LikeTargetType.fromCode(po.getTargetType()))
                .createdAt(po.getCreatedAt())
                .build();
    }

    private LikePO toLikePO(Like like) {
        if (like == null) {
            return null;
        }
        return LikePO.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .userName(like.getUserName())
                .userType(like.getUserType())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType() != null ? like.getTargetType().getCode() : null)
                .createdAt(like.getCreatedAt())
                .build();
    }

    // ==================== 仓储接口实现 ====================

    @Override
    public Like save(Like like) {
        LikePO po = toLikePO(like);
        if (po.getCreatedAt() == null) {
            po.setCreatedAt(LocalDateTime.now());
        }
        likeMapper.insert(po);
        return toLike(po);
    }

    @Override
    public void delete(Long id) {
        likeMapper.deleteById(id);
    }

    @Override
    public Like findById(Long id) {
        LikePO po = likeMapper.selectById(id);
        return toLike(po);
    }

    @Override
    public Like findByUserAndTarget(Long userId, String userType, Long targetId, LikeTargetType targetType) {
        LikePO po = likeMapper.selectByUserAndTarget(userId, userType, targetId, targetType.getCode());
        return toLike(po);
    }

    @Override
    public boolean existsByUserAndTarget(Long userId, String userType, Long targetId, LikeTargetType targetType) {
        return likeMapper.existsByUserAndTarget(userId, userType, targetId, targetType.getCode()) > 0;
    }

    @Override
    public List<Like> findByTarget(Long targetId, LikeTargetType targetType) {
        List<LikePO> pos = likeMapper.selectByTarget(targetId, targetType.getCode());
        return pos.stream().map(this::toLike).collect(Collectors.toList());
    }

    @Override
    public List<Like> findByUser(Long userId, String userType) {
        List<LikePO> pos = likeMapper.selectByUser(userId, userType);
        return pos.stream().map(this::toLike).collect(Collectors.toList());
    }

    @Override
    public long countByTarget(Long targetId, LikeTargetType targetType) {
        return likeMapper.countByTarget(targetId, targetType.getCode());
    }

    @Override
    public long countByUser(Long userId, String userType) {
        return likeMapper.countByUser(userId, userType);
    }

    @Override
    public List<Like> findByTargetWithPage(Long targetId, LikeTargetType targetType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<LikePO> pos = likeMapper.selectByTargetWithPage(targetId, targetType.getCode(), offset, pageSize);
        return pos.stream().map(this::toLike).collect(Collectors.toList());
    }

    @Override
    public List<Like> findByUserWithPage(Long userId, String userType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<LikePO> pos = likeMapper.selectByUserWithPage(userId, userType, offset, pageSize);
        return pos.stream().map(this::toLike).collect(Collectors.toList());
    }

    @Override
    public Long nextId() {
        return idGenerator.nextId();
    }

    @Override
    public void incrementTargetLikeCount(Long targetId, LikeTargetType targetType) {
        // TODO: 更新目标的点赞计数字段（articles.like_count 或 comments.like_count）
        // 这里可以通过事件机制或者直接调用对应的 Mapper
        log.debug("增加目标点赞计数: targetId={}, targetType={}", targetId, targetType);
    }

    @Override
    public void decrementTargetLikeCount(Long targetId, LikeTargetType targetType) {
        // TODO: 更新目标的点赞计数字段
        log.debug("减少目标点赞计数: targetId={}, targetType={}", targetId, targetType);
    }

    @Override
    public void deleteOneByTarget(Long targetId, LikeTargetType targetType) {
        likeMapper.deleteOneByTarget(targetId, targetType.getCode());
    }
}