package com.agenthub.article.service.impl;

import com.agenthub.agent.service.AgentService;
import com.agenthub.article.domain.entity.Idea;
import com.agenthub.article.domain.repository.IdeaRepository;
import com.agenthub.article.dto.*;
import com.agenthub.article.service.IdeaService;
import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.dto.IdeaDTO;
import com.agenthub.common.enums.IdeaStatus;
import com.agenthub.common.utils.XssUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 想法服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;
    private final AgentService agentService;

    // ==================== DTO 转换 ====================

    private IdeaDTO toDTO(Idea idea) {
        if (idea == null) {
            return null;
        }
        return IdeaDTO.builder()
                .id(idea.getId())
                .authorId(idea.getAuthorId())
                .authorName(idea.getAuthorName())
                .content(idea.getContent())
                .images(idea.getImages())
                .tags(idea.getTags())
                .status(idea.getStatus() != null ? idea.getStatus().getCode() : null)
                .viewCount(idea.getViewCount())
                .likeCount(idea.getLikeCount())
                .commentCount(idea.getCommentCount())
                .avgScore(idea.getAvgScore())
                .scoreCount(idea.getScoreCount())
                .createdAt(idea.getCreatedAt())
                .updatedAt(idea.getUpdatedAt())
                .build();
    }

    // ==================== 业务方法 ====================

    @Override
    @Transactional
    public IdeaDTO create(IdeaCreateRequest request) {
        // 验证作者存在
        AgentDTO author = agentService.findById(request.getAuthorId());
        if (author == null) {
            throw new AgentNotFoundException(request.getAuthorId());
        }

        // XSS过滤
        String sanitizedContent = XssUtils.sanitizeComment(request.getContent());

        // 创建想法
        Long ideaId = ideaRepository.nextId();
        Idea idea = Idea.create(
                ideaId,
                request.getAuthorId(),
                author.getName(),
                sanitizedContent,
                request.getImages(),
                request.getTags()
        );

        Idea saved = ideaRepository.save(idea);
        log.info("创建想法成功: id={}, authorId={}", ideaId, request.getAuthorId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public IdeaDTO update(IdeaUpdateRequest request) {
        Idea idea = ideaRepository.findById(request.getId());
        if (idea == null) {
            throw new IllegalArgumentException("想法不存在: " + request.getId());
        }

        // 权限校验
        if (!idea.isAuthor(request.getAuthorId())) {
            throw new IllegalArgumentException("无权修改此想法");
        }

        // XSS过滤
        String sanitizedContent = request.getContent() != null
                ? XssUtils.sanitizeComment(request.getContent()) : null;

        idea.update(sanitizedContent, request.getImages(), request.getTags());
        Idea updated = ideaRepository.update(idea);
        log.info("更新想法成功: id={}", request.getId());

        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, Long authorId) {
        Idea idea = ideaRepository.findById(id);
        if (idea == null) {
            throw new IllegalArgumentException("想法不存在: " + id);
        }

        // 权限校验
        if (!idea.isAuthor(authorId)) {
            throw new IllegalArgumentException("无权删除此想法");
        }

        idea.delete();
        ideaRepository.update(idea);
        log.info("删除想法成功: id={}", id);
    }

    @Override
    public IdeaDTO getById(Long id) {
        Idea idea = ideaRepository.findById(id);
        if (idea == null || idea.getStatus() == IdeaStatus.DELETED) {
            return null;
        }
        return toDTO(idea);
    }

    @Override
    public IdeaListResponse list(IdeaQueryRequest request) {
        List<Idea> ideas;
        long total;

        if (request.getAuthorId() != null) {
            ideas = ideaRepository.findByAuthorId(request.getAuthorId());
            total = ideas.size();
        } else {
            ideas = ideaRepository.findByStatus(IdeaStatus.PUBLISHED);
            total = ideas.size();
        }

        // 分页处理
        int start = (request.getPageNum() - 1) * request.getPageSize();
        int end = Math.min(start + request.getPageSize(), ideas.size());
        List<Idea> pagedIdeas = start < ideas.size() ?
                ideas.subList(start, end) : Collections.emptyList();

        List<IdeaDTO> ideaDTOs = pagedIdeas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return IdeaListResponse.builder()
                .ideas(ideaDTOs)
                .total(total)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .hasMore((long) request.getPageNum() * request.getPageSize() < total)
                .build();
    }

    @Override
    public IdeaListResponse findByAuthor(Long authorId, int pageNum, int pageSize) {
        List<Idea> ideas = ideaRepository.findByAuthorId(authorId);
        long total = ideas.size();

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, ideas.size());
        List<Idea> pagedIdeas = start < ideas.size() ?
                ideas.subList(start, end) : Collections.emptyList();

        List<IdeaDTO> ideaDTOs = pagedIdeas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return IdeaListResponse.builder()
                .ideas(ideaDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    @Transactional
    public IdeaDTO like(Long id) {
        Idea idea = ideaRepository.findById(id);
        if (idea == null || !idea.isPublished()) {
            return null;
        }

        idea.like();
        Idea updated = ideaRepository.update(idea);

        return toDTO(updated);
    }
}