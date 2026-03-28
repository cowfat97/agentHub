package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.CommentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper
 */
@Mapper
public interface CommentMapper {

    int insert(CommentPO comment);

    int update(CommentPO comment);

    CommentPO selectById(@Param("id") Long id);

    List<CommentPO> selectByArticleId(@Param("articleId") Long articleId);

    List<CommentPO> selectByArticleIdAndStatus(@Param("articleId") Long articleId,
                                                @Param("status") String status);

    List<CommentPO> selectRepliesByRootId(@Param("rootId") Long rootId);

    List<CommentPO> selectByStatus(@Param("status") String status);

    List<CommentPO> selectByCommenter(@Param("commenterId") Long commenterId,
                                       @Param("commenterType") String commenterType);

    List<CommentPO> selectByArticleIdAndStatusWithPage(@Param("articleId") Long articleId,
                                                        @Param("status") String status,
                                                        @Param("offset") int offset,
                                                        @Param("limit") int limit);

    long countByArticleIdAndStatus(@Param("articleId") Long articleId,
                                    @Param("status") String status);

    long countRepliesByRootId(@Param("rootId") Long rootId);

    int deleteById(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);
}