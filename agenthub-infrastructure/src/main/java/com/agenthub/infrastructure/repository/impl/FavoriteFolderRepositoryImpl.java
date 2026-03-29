package com.agenthub.infrastructure.repository.impl;

import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.FavoriteFolderPO;
import com.agenthub.infrastructure.mapper.FavoriteFolderMapper;
import com.agenthub.recommendation.domain.entity.FavoriteFolder;
import com.agenthub.recommendation.domain.repository.FavoriteFolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏夹仓储实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FavoriteFolderRepositoryImpl implements FavoriteFolderRepository {

    private final FavoriteFolderMapper folderMapper;

    @Override
    public Long nextId() {
        return SnowflakeIdGenerator.getInstance().nextId();
    }

    @Override
    public FavoriteFolder save(FavoriteFolder folder) {
        FavoriteFolderPO po = toPO(folder);
        folderMapper.insert(po);
        return folder;
    }

    @Override
    public FavoriteFolder update(FavoriteFolder folder) {
        FavoriteFolderPO po = toPO(folder);
        folderMapper.update(po);
        return folder;
    }

    @Override
    public FavoriteFolder findById(Long id) {
        FavoriteFolderPO po = folderMapper.findById(id);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public List<FavoriteFolder> findByUser(Long userId, String userType) {
        return folderMapper.findByUser(userId, userType).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FavoriteFolder findDefaultFolder(Long userId, String userType) {
        FavoriteFolderPO po = folderMapper.findDefaultFolder(userId, userType);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public void delete(Long id) {
        folderMapper.delete(id);
    }

    @Override
    public long countByUser(Long userId, String userType) {
        return folderMapper.countByUser(userId, userType);
    }

    private FavoriteFolderPO toPO(FavoriteFolder folder) {
        FavoriteFolderPO po = new FavoriteFolderPO();
        po.setId(folder.getId());
        po.setUserId(folder.getUserId());
        po.setUserType(folder.getUserType());
        po.setName(folder.getName());
        po.setDescription(folder.getDescription());
        po.setIsDefault(folder.getIsDefault());
        po.setSortOrder(folder.getSortOrder());
        po.setItemCount(folder.getItemCount());
        po.setCreatedAt(folder.getCreatedAt());
        po.setUpdatedAt(folder.getUpdatedAt());
        return po;
    }

    private FavoriteFolder toEntity(FavoriteFolderPO po) {
        return FavoriteFolder.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .userType(po.getUserType())
                .name(po.getName())
                .description(po.getDescription())
                .isDefault(po.getIsDefault())
                .sortOrder(po.getSortOrder())
                .itemCount(po.getItemCount())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
}