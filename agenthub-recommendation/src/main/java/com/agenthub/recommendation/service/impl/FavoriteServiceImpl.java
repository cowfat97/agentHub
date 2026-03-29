package com.agenthub.recommendation.service.impl;

import com.agenthub.common.enums.FavoriteTargetType;
import com.agenthub.recommendation.domain.entity.Favorite;
import com.agenthub.recommendation.domain.entity.FavoriteFolder;
import com.agenthub.recommendation.domain.repository.FavoriteFolderRepository;
import com.agenthub.recommendation.domain.repository.FavoriteRepository;
import com.agenthub.recommendation.dto.*;
import com.agenthub.recommendation.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteFolderRepository folderRepository;
    private final FavoriteRepository favoriteRepository;

    // ==================== 收藏夹管理 ====================

    @Override
    @Transactional
    public FolderDTO createFolder(FolderCreateRequest request) {
        FavoriteFolder folder = FavoriteFolder.create(
                folderRepository.nextId(),
                request.getUserId(),
                request.getUserType(),
                request.getName(),
                request.getDescription(),
                false
        );
        FavoriteFolder saved = folderRepository.save(folder);
        log.info("创建收藏夹: id={}, userId={}, name={}", saved.getId(), request.getUserId(), request.getName());
        return toFolderDTO(saved);
    }

    @Override
    @Transactional
    public FolderDTO updateFolder(Long id, Long userId, String userType, String name, String description) {
        FavoriteFolder folder = folderRepository.findById(id);
        if (folder == null) {
            throw new IllegalArgumentException("收藏夹不存在");
        }
        if (!folder.isOwner(userId, userType)) {
            throw new IllegalArgumentException("无权修改此收藏夹");
        }
        folder.update(name, description);
        FavoriteFolder updated = folderRepository.update(folder);
        return toFolderDTO(updated);
    }

    @Override
    @Transactional
    public void deleteFolder(Long id, Long userId, String userType) {
        FavoriteFolder folder = folderRepository.findById(id);
        if (folder == null) {
            return;
        }
        if (!folder.isOwner(userId, userType)) {
            throw new IllegalArgumentException("无权删除此收藏夹");
        }
        folderRepository.delete(id);
        log.info("删除收藏夹: id={}", id);
    }

    @Override
    public List<FolderDTO> getUserFolders(Long userId, String userType) {
        return folderRepository.findByUser(userId, userType).stream()
                .map(this::toFolderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FolderDTO getFolderById(Long id) {
        FavoriteFolder folder = folderRepository.findById(id);
        return folder != null ? toFolderDTO(folder) : null;
    }

    // ==================== 收藏操作 ====================

    @Override
    @Transactional
    public void addFavorite(FavoriteRequest request) {
        // 检查是否已收藏
        if (favoriteRepository.existsByUserAndTarget(
                request.getUserId(), request.getUserType(),
                request.getTargetId(), request.getTargetType())) {
            log.info("已收藏，跳过: userId={}, targetId={}", request.getUserId(), request.getTargetId());
            return;
        }

        // 验证收藏夹
        FavoriteFolder folder = folderRepository.findById(request.getFolderId());
        if (folder == null) {
            throw new IllegalArgumentException("收藏夹不存在");
        }

        // 创建收藏
        Favorite favorite = Favorite.create(
                favoriteRepository.nextId(),
                request.getFolderId(),
                request.getUserId(),
                request.getUserType(),
                request.getTargetId(),
                FavoriteTargetType.fromCode(request.getTargetType())
        );
        favoriteRepository.save(favorite);

        // 更新收藏夹计数
        folder.incrementCount();
        folderRepository.update(folder);

        log.info("添加收藏: userId={}, targetId={}, folderId={}", request.getUserId(), request.getTargetId(), request.getFolderId());
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, String userType, Long targetId, String targetType) {
        Favorite favorite = favoriteRepository.findByUserAndTarget(userId, userType, targetId, targetType);
        if (favorite == null) {
            return;
        }

        // 更新收藏夹计数
        FavoriteFolder folder = folderRepository.findById(favorite.getFolderId());
        if (folder != null) {
            folder.decrementCount();
            folderRepository.update(folder);
        }

        // 删除收藏
        favoriteRepository.deleteByUserAndTarget(userId, userType, targetId, targetType);
        log.info("取消收藏: userId={}, targetId={}", userId, targetId);
    }

    @Override
    @Transactional
    public void moveFavorite(Long userId, String userType, Long targetId, String targetType, Long newFolderId) {
        Favorite favorite = favoriteRepository.findByUserAndTarget(userId, userType, targetId, targetType);
        if (favorite == null) {
            throw new IllegalArgumentException("未找到收藏记录");
        }

        Long oldFolderId = favorite.getFolderId();

        // 更新收藏夹计数
        FavoriteFolder oldFolder = folderRepository.findById(oldFolderId);
        if (oldFolder != null) {
            oldFolder.decrementCount();
            folderRepository.update(oldFolder);
        }

        FavoriteFolder newFolder = folderRepository.findById(newFolderId);
        if (newFolder != null) {
            newFolder.incrementCount();
            folderRepository.update(newFolder);
        }

        // 移动收藏
        favorite.moveTo(newFolderId);
        log.info("移动收藏: userId={}, targetId={}, from={}, to={}", userId, targetId, oldFolderId, newFolderId);
    }

    @Override
    public FavoriteStatusResponse getFavoriteStatus(Long userId, String userType, Long targetId, String targetType) {
        Favorite favorite = favoriteRepository.findByUserAndTarget(userId, userType, targetId, targetType);

        if (favorite == null) {
            return FavoriteStatusResponse.builder()
                    .isFavorited(false)
                    .build();
        }

        FavoriteFolder folder = folderRepository.findById(favorite.getFolderId());
        return FavoriteStatusResponse.builder()
                .isFavorited(true)
                .folderId(favorite.getFolderId())
                .folderName(folder != null ? folder.getName() : null)
                .createdAt(favorite.getCreatedAt())
                .build();
    }

    @Override
    public boolean isFavorited(Long userId, String userType, Long targetId, String targetType) {
        return favoriteRepository.existsByUserAndTarget(userId, userType, targetId, targetType);
    }

    // ==================== DTO转换 ====================

    private FolderDTO toFolderDTO(FavoriteFolder folder) {
        return FolderDTO.builder()
                .id(folder.getId())
                .userId(folder.getUserId())
                .userType(folder.getUserType())
                .name(folder.getName())
                .description(folder.getDescription())
                .isDefault(folder.getIsDefault())
                .sortOrder(folder.getSortOrder())
                .itemCount(folder.getItemCount())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }
}