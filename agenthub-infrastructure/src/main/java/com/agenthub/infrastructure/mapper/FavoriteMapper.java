package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.FavoritePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏Mapper
 */
@Mapper
public interface FavoriteMapper {

    int insert(FavoritePO favorite);

    FavoritePO findById(@Param("id") Long id);

    FavoritePO findByUserAndTarget(@Param("userId") Long userId,
                                   @Param("userType") String userType,
                                   @Param("targetId") Long targetId,
                                   @Param("targetType") String targetType);

    List<FavoritePO> findByFolder(@Param("folderId") Long folderId);

    List<FavoritePO> findByUser(@Param("userId") Long userId,
                                @Param("userType") String userType);

    void delete(@Param("id") Long id);

    void deleteByUserAndTarget(@Param("userId") Long userId,
                               @Param("userType") String userType,
                               @Param("targetId") Long targetId,
                               @Param("targetType") String targetType);

    long countByFolder(@Param("folderId") Long folderId);

    boolean existsByUserAndTarget(@Param("userId") Long userId,
                                  @Param("userType") String userType,
                                  @Param("targetId") Long targetId,
                                  @Param("targetType") String targetType);
}