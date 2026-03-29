package com.agenthub.infrastructure.repository.impl;

import com.agenthub.common.enums.FavoriteTargetType;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.FavoritePO;
import com.agenthub.infrastructure.mapper.FavoriteMapper;
import com.agenthub.recommendation.domain.entity.Favorite;
import com.agenthub.recommendation.domain.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏仓储实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final FavoriteMapper favoriteMapper;

    @Override
    public Long nextId() {
        return SnowflakeIdGenerator.getInstance().nextId();
    }

    @Override
    public Favorite save(Favorite favorite) {
        FavoritePO po = toPO(favorite);
        favoriteMapper.insert(po);
        return favorite;
    }

    @Override
    public Favorite findById(Long id) {
        FavoritePO po = favoriteMapper.findById(id);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public Favorite findByUserAndTarget(Long userId, String userType, Long targetId, String targetType) {
        FavoritePO po = favoriteMapper.findByUserAndTarget(userId, userType, targetId, targetType);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public List<Favorite> findByFolder(Long folderId) {
        return favoriteMapper.findByFolder(folderId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Favorite> findByUser(Long userId, String userType) {
        return favoriteMapper.findByUser(userId, userType).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        favoriteMapper.delete(id);
    }

    @Override
    public void deleteByUserAndTarget(Long userId, String userType, Long targetId, String targetType) {
        favoriteMapper.deleteByUserAndTarget(userId, userType, targetId, targetType);
    }

    @Override
    public long countByFolder(Long folderId) {
        return favoriteMapper.countByFolder(folderId);
    }

    @Override
    public boolean existsByUserAndTarget(Long userId, String userType, Long targetId, String targetType) {
        return favoriteMapper.existsByUserAndTarget(userId, userType, targetId, targetType);
    }

    private FavoritePO toPO(Favorite favorite) {
        FavoritePO po = new FavoritePO();
        po.setId(favorite.getId());
        po.setFolderId(favorite.getFolderId());
        po.setUserId(favorite.getUserId());
        po.setUserType(favorite.getUserType());
        po.setTargetId(favorite.getTargetId());
        po.setTargetType(favorite.getTargetType() != null ? favorite.getTargetType().getCode() : null);
        po.setCreatedAt(favorite.getCreatedAt());
        return po;
    }

    private Favorite toEntity(FavoritePO po) {
        return Favorite.builder()
                .id(po.getId())
                .folderId(po.getFolderId())
                .userId(po.getUserId())
                .userType(po.getUserType())
                .targetId(po.getTargetId())
                .targetType(FavoriteTargetType.fromCode(po.getTargetType()))
                .createdAt(po.getCreatedAt())
                .build();
    }
}