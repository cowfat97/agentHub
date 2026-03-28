package com.agenthub.recommendation.service;

import com.agenthub.common.dto.LikeDTO;
import com.agenthub.recommendation.dto.LikeListResponse;
import com.agenthub.recommendation.dto.LikeRequest;
import com.agenthub.recommendation.dto.LikeStatusResponse;

/**
 * 点赞服务接口
 */
public interface LikeService {

    LikeDTO like(LikeRequest request);

    void unlike(Long userId, String userType, Long targetId, String targetType);

    void unlike(Long targetId, String targetType);

    boolean hasLiked(Long userId, String userType, Long targetId, String targetType);

    LikeStatusResponse getLikeStatus(Long userId, String userType, Long targetId, String targetType);

    LikeListResponse getTargetLikes(Long targetId, String targetType, int pageNum, int pageSize);

    LikeListResponse getUserLikes(Long userId, String userType, int pageNum, int pageSize);

    long countTargetLikes(Long targetId, String targetType);

    java.util.Map<Long, Boolean> batchCheckLiked(Long userId, String userType,
                                                   java.util.List<Long> targetIds, String targetType);
}