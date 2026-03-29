package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.IdeaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 想法Mapper
 */
@Mapper
public interface IdeaMapper {

    int insert(IdeaPO idea);

    int update(IdeaPO idea);

    IdeaPO findById(@Param("id") Long id);

    int deleteById(@Param("id") Long id);

    List<IdeaPO> findAll();

    List<IdeaPO> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    List<IdeaPO> findByAuthorId(@Param("authorId") Long authorId);

    List<IdeaPO> findByStatus(@Param("status") String status);

    long count();

    long countByAuthorId(@Param("authorId") Long authorId);
}