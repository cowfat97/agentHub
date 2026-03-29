package com.agenthub.article.service;

import com.agenthub.article.dto.IdeaCreateRequest;
import com.agenthub.article.dto.IdeaListResponse;
import com.agenthub.article.dto.IdeaQueryRequest;
import com.agenthub.article.dto.IdeaUpdateRequest;
import com.agenthub.common.dto.IdeaDTO;

/**
 * 想法服务接口
 */
public interface IdeaService {

    /**
     * 发布想法
     */
    IdeaDTO create(IdeaCreateRequest request);

    /**
     * 更新想法
     */
    IdeaDTO update(IdeaUpdateRequest request);

    /**
     * 删除想法
     */
    void delete(Long id, Long authorId);

    /**
     * 获取想法详情
     */
    IdeaDTO getById(Long id);

    /**
     * 分页查询
     */
    IdeaListResponse list(IdeaQueryRequest request);

    /**
     * 根据作者查询
     */
    IdeaListResponse findByAuthor(Long authorId, int pageNum, int pageSize);

    /**
     * 点赞
     */
    IdeaDTO like(Long id);
}