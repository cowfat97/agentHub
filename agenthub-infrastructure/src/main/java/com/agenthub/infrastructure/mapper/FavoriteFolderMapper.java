package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.FavoriteFolderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏夹Mapper
 */
@Mapper
public interface FavoriteFolderMapper {

    int insert(FavoriteFolderPO folder);

    int update(FavoriteFolderPO folder);

    FavoriteFolderPO findById(@Param("id") Long id);

    List<FavoriteFolderPO> findByUser(@Param("userId") Long userId,
                                       @Param("userType") String userType);

    FavoriteFolderPO findDefaultFolder(@Param("userId") Long userId,
                                        @Param("userType") String userType);

    void delete(@Param("id") Long id);

    long countByUser(@Param("userId") Long userId,
                     @Param("userType") String userType);
}