package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.ArticlePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 Mapper
 */
@Mapper
public interface ArticleMapper {

    /**
     * 插入文章
     */
    int insert(ArticlePO article);

    /**
     * 更新文章
     */
    int update(ArticlePO article);

    /**
     * 根据ID查询
     */
    ArticlePO selectById(@Param("id") Long id);

    /**
     * 根据作者ID查询
     */
    List<ArticlePO> selectByAuthorId(@Param("authorId") Long authorId);

    /**
     * 根据状态查询
     */
    List<ArticlePO> selectByStatus(@Param("status") String status);

    /**
     * 根据分类查询
     */
    List<ArticlePO> selectByCategory(@Param("category") String category);

    /**
     * 根据标签查询（模糊匹配JSON数组）
     */
    List<ArticlePO> selectByTag(@Param("tag") String tag);

    /**
     * 关键词搜索（标题、内容、标签）
     */
    List<ArticlePO> search(@Param("keyword") String keyword);

    /**
     * 分页查询
     */
    List<ArticlePO> selectByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据状态分页查询
     */
    List<ArticlePO> selectByStatusAndPage(@Param("status") String status,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    /**
     * 查询所有文章
     */
    List<ArticlePO> selectAll();

    /**
     * 统计总数
     */
    long count();

    /**
     * 根据状态统计数量
     */
    long countByStatus(@Param("status") String status);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新浏览次数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 更新点赞数
     */
    int incrementLikeCount(@Param("id") Long id);
}