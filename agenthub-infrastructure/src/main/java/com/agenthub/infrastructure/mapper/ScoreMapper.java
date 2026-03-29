package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.ScorePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评分Mapper
 */
@Mapper
public interface ScoreMapper {

    int insert(ScorePO score);

    int update(ScorePO score);

    ScorePO findById(@Param("id") Long id);

    ScorePO findByUserAndTarget(@Param("userId") Long userId,
                                @Param("userType") String userType,
                                @Param("targetId") Long targetId,
                                @Param("targetType") String targetType);

    void delete(@Param("id") Long id);

    List<ScorePO> findByTarget(@Param("targetId") Long targetId,
                               @Param("targetType") String targetType);

    long countByTarget(@Param("targetId") Long targetId,
                       @Param("targetType") String targetType);

    Double getAvgScore(@Param("targetId") Long targetId,
                       @Param("targetType") String targetType);
}