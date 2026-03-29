package com.agenthub.article.domain.repository;

import com.agenthub.article.domain.entity.Idea;
import com.agenthub.common.enums.IdeaStatus;

import java.util.List;

/**
 * 想法仓储接口
 */
public interface IdeaRepository {

    /**
     * 生成下一个ID
     */
    Long nextId();

    /**
     * 保存想法
     */
    Idea save(Idea idea);

    /**
     * 更新想法
     */
    Idea update(Idea idea);

    /**
     * 根据ID查询
     */
    Idea findById(Long id);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);

    /**
     * 查询所有
     */
    List<Idea> findAll();

    /**
     * 分页查询
     */
    List<Idea> findByPage(int pageNum, int pageSize);

    /**
     * 根据作者查询
     */
    List<Idea> findByAuthorId(Long authorId);

    /**
     * 根据状态查询
     */
    List<Idea> findByStatus(IdeaStatus status);

    /**
     * 统计总数
     */
    long count();

    /**
     * 根据作者统计
     */
    long countByAuthorId(Long authorId);
}