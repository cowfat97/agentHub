package com.agenthub.infrastructure.repository.impl;

import com.agenthub.article.domain.entity.Idea;
import com.agenthub.article.domain.repository.IdeaRepository;
import com.agenthub.common.enums.IdeaStatus;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.IdeaPO;
import com.agenthub.infrastructure.mapper.IdeaMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 想法仓储实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class IdeaRepositoryImpl implements IdeaRepository {

    private final IdeaMapper ideaMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Long nextId() {
        return SnowflakeIdGenerator.getInstance().nextId();
    }

    @Override
    public Idea save(Idea idea) {
        IdeaPO po = toPO(idea);
        ideaMapper.insert(po);
        return idea;
    }

    @Override
    public Idea update(Idea idea) {
        IdeaPO po = toPO(idea);
        ideaMapper.update(po);
        return idea;
    }

    @Override
    public Idea findById(Long id) {
        IdeaPO po = ideaMapper.findById(id);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public void deleteById(Long id) {
        ideaMapper.deleteById(id);
    }

    @Override
    public List<Idea> findAll() {
        return ideaMapper.findAll().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Idea> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return ideaMapper.findByPage(offset, pageSize).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Idea> findByAuthorId(Long authorId) {
        return ideaMapper.findByAuthorId(authorId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Idea> findByStatus(IdeaStatus status) {
        return ideaMapper.findByStatus(status.getCode()).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return ideaMapper.count();
    }

    @Override
    public long countByAuthorId(Long authorId) {
        return ideaMapper.countByAuthorId(authorId);
    }

    // ==================== 转换方法 ====================

    private IdeaPO toPO(Idea idea) {
        IdeaPO po = new IdeaPO();
        po.setId(idea.getId());
        po.setAuthorId(idea.getAuthorId());
        po.setAuthorName(idea.getAuthorName());
        po.setContent(idea.getContent());
        po.setImages(toJson(idea.getImages()));
        po.setTags(toJson(idea.getTags()));
        po.setStatus(idea.getStatus() != null ? idea.getStatus().getCode() : null);
        po.setViewCount(idea.getViewCount());
        po.setLikeCount(idea.getLikeCount());
        po.setCommentCount(idea.getCommentCount());
        po.setAvgScore(idea.getAvgScore() != null ? idea.getAvgScore() : BigDecimal.ZERO);
        po.setScoreCount(idea.getScoreCount());
        po.setCreatedAt(idea.getCreatedAt());
        po.setUpdatedAt(idea.getUpdatedAt());
        return po;
    }

    private Idea toEntity(IdeaPO po) {
        return Idea.builder()
                .id(po.getId())
                .authorId(po.getAuthorId())
                .authorName(po.getAuthorName())
                .content(po.getContent())
                .images(parseJsonList(po.getImages()))
                .tags(parseJsonList(po.getTags()))
                .status(IdeaStatus.fromCode(po.getStatus()))
                .viewCount(po.getViewCount())
                .likeCount(po.getLikeCount())
                .commentCount(po.getCommentCount())
                .avgScore(po.getAvgScore())
                .scoreCount(po.getScoreCount())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return null;
        }
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败", e);
            return Collections.emptyList();
        }
    }
}