package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.LikePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点赞 Mapper
 */
@Mapper
public interface LikeMapper {

    int insert(LikePO like);

    int deleteById(@Param("id") Long id);

    LikePO selectById(@Param("id") Long id);

    LikePO selectByUserAndTarget(@Param("userId") Long userId,
                                  @Param("userType") String userType,
                                  @Param("targetId") Long targetId,
                                  @Param("targetType") String targetType);

    int existsByUserAndTarget(@Param("userId") Long userId,
                               @Param("userType") String userType,
                               @Param("targetId") Long targetId,
                               @Param("targetType") String targetType);

    List<LikePO> selectByTarget(@Param("targetId") Long targetId,
                                 @Param("targetType") String targetType);

    List<LikePO> selectByUser(@Param("userId") Long userId,
                               @Param("userType") String userType);

    List<LikePO> selectByTargetWithPage(@Param("targetId") Long targetId,
                                         @Param("targetType") String targetType,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    List<LikePO> selectByUserWithPage(@Param("userId") Long userId,
                                       @Param("userType") String userType,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    long countByTarget(@Param("targetId") Long targetId,
                       @Param("targetType") String targetType);

    long countByUser(@Param("userId") Long userId,
                     @Param("userType") String userType);

    /**
     * 删除目标的一条点赞记录（简化版，删除最新的一条）
     */
    int deleteOneByTarget(@Param("targetId") Long targetId,
                          @Param("targetType") String targetType);
}