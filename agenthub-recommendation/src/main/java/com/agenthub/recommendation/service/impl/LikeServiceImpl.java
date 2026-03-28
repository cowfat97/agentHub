package com.agenthub.recommendation.service.impl;

import com.agenthub.common.domain.exception.DuplicateLikeException;
import com.agenthub.common.dto.LikeDTO;
import com.agenthub.common.enums.LikeTargetType;
import com.agenthub.recommendation.domain.entity.Like;
import com.agenthub.recommendation.domain.repository.LikeRepository;
import com.agenthub.recommendation.dto.LikeListResponse;
import com.agenthub.recommendation.dto.LikeRequest;
import com.agenthub.recommendation.dto.LikeStatusResponse;
import com.agenthub.recommendation.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点赞服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private LikeDTO toDTO(Like like) {
        if (like == null) {
            return null;
        }
        return LikeDTO.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .userName(like.getUserName())
                .userType(like.getUserType())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType() != null ? like.getTargetType().getCode() : null)
                .createdAt(like.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public LikeDTO like(LikeRequest request) {
        LikeTargetType targetType = LikeTargetType.fromCode(request.getTargetType());
        if (targetType == null) {
            throw new IllegalArgumentException("无效的目标类型: " + request.getTargetType());
        }

        if (request.getUserId() != null && likeRepository.existsByUserAndTarget(
                request.getUserId(), request.getUserType(),
                request.getTargetId(), targetType)) {
            throw new DuplicateLikeException(request.getUserId(), request.getTargetId(), request.getTargetType());
        }

        Long likeId = likeRepository.nextId();
        Like like = Like.create(
                likeId,
                request.getUserId(),
                request.getUserName(),
                request.getUserType(),
                request.getTargetId(),
                targetType
        );

        Like saved = likeRepository.save(like);
        likeRepository.incrementTargetLikeCount(request.getTargetId(), targetType);

        log.info("点赞成功: userId={}, targetId={}, targetType={}",
                request.getUserId(), request.getTargetId(), request.getTargetType());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public void unlike(Long userId, String userType, Long targetId, String targetType) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        if (type == null) {
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }

        Like like = likeRepository.findByUserAndTarget(userId, userType, targetId, type);
        if (like == null) {
            log.warn("取消点赞失败：点赞记录不存在, userId={}, targetId={}", userId, targetId);
            return;
        }

        likeRepository.delete(like.getId());
        likeRepository.decrementTargetLikeCount(targetId, type);

        log.info("取消点赞成功: userId={}, targetId={}, targetType={}", userId, targetId, targetType);
    }

    @Override
    @Transactional
    public void unlike(Long targetId, String targetType) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        if (type == null) {
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }

        likeRepository.deleteOneByTarget(targetId, type);
        likeRepository.decrementTargetLikeCount(targetId, type);

        log.info("取消点赞成功（简化版）: targetId={}, targetType={}", targetId, targetType);
    }

    @Override
    public boolean hasLiked(Long userId, String userType, Long targetId, String targetType) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        if (type == null) {
            return false;
        }
        return likeRepository.existsByUserAndTarget(userId, userType, targetId, type);
    }

    @Override
    public LikeStatusResponse getLikeStatus(Long userId, String userType, Long targetId, String targetType) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        if (type == null) {
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }

        boolean liked = userId != null && likeRepository.existsByUserAndTarget(userId, userType, targetId, type);
        long count = likeRepository.countByTarget(targetId, type);

        return LikeStatusResponse.builder()
                .targetId(targetId)
                .targetType(targetType)
                .liked(liked)
                .likeCount(count)
                .build();
    }

    @Override
    public LikeListResponse getTargetLikes(Long targetId, String targetType, int pageNum, int pageSize) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        if (type == null) {
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }

        List<Like> likes = likeRepository.findByTargetWithPage(targetId, type, pageNum, pageSize);
        long total = likeRepository.countByTarget(targetId, type);

        List<LikeDTO> likeDTOs = likes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return LikeListResponse.builder()
                .likes(likeDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public LikeListResponse getUserLikes(Long userId, String userType, int pageNum, int pageSize) {
        List<Like> likes = likeRepository.findByUserWithPage(userId, userType, pageNum, pageSize);
        long total = likeRepository.countByUser(userId, userType);

        List<LikeDTO> likeDTOs = likes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return LikeListResponse.builder()
                .likes(likeDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public long countTargetLikes(Long targetId, String targetType) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        if (type == null) {
            return 0;
        }
        return likeRepository.countByTarget(targetId, type);
    }

    @Override
    public Map<Long, Boolean> batchCheckLiked(Long userId, String userType,
                                                List<Long> targetIds, String targetType) {
        LikeTargetType type = LikeTargetType.fromCode(targetType);
        Map<Long, Boolean> result = new HashMap<>();

        if (type == null) {
            targetIds.forEach(id -> result.put(id, false));
            return result;
        }

        for (Long targetId : targetIds) {
            boolean liked = userId != null && likeRepository.existsByUserAndTarget(userId, userType, targetId, type);
            result.put(targetId, liked);
        }

        return result;
    }
}