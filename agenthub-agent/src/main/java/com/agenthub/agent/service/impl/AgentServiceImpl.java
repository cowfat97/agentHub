package com.agenthub.agent.service.impl;

import com.agenthub.agent.domain.entity.Agent;
import com.agenthub.agent.domain.repository.AgentRepository;
import com.agenthub.agent.domain.valueobject.AgentInfo;
import com.agenthub.agent.dto.*;
import com.agenthub.agent.service.AgentService;
import com.agenthub.common.domain.event.DomainEventPublisher;
import com.agenthub.common.domain.exception.AgentAlreadyExistsException;
import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.enums.AgentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent 服务实现
 *
 * 统一实现注册和发现功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final DomainEventPublisher eventPublisher;

    // ==================== DTO 转换 ====================

    private AgentDTO toDTO(Agent agent) {
        if (agent == null) {
            return null;
        }
        return AgentDTO.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .endpoint(agent.getEndpoint())
                .version(agent.getVersion())
                .status(agent.getStatus() != null ? agent.getStatus().name() : null)
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }

    private AgentDTO toDTO(AgentInfo agentInfo) {
        if (agentInfo == null) {
            return null;
        }
        return AgentDTO.builder()
                .id(agentInfo.getId())
                .name(agentInfo.getName())
                .description(agentInfo.getDescription())
                .endpoint(agentInfo.getEndpoint())
                .version(agentInfo.getVersion())
                .status(agentInfo.getStatus() != null ? agentInfo.getStatus().name() : null)
                .createdAt(agentInfo.getCreatedAt())
                .updatedAt(agentInfo.getUpdatedAt())
                .build();
    }

    // ==================== 注册功能 ====================

    @Override
    @Transactional
    public AgentDTO register(AgentRegisterRequest request) {
        // 检查名称是否已存在
        if (agentRepository.existsByName(request.getName())) {
            throw new AgentAlreadyExistsException(request.getName());
        }

        // 生成ID
        Long id = agentRepository.nextId();

        // 创建 Agent 实体
        Agent agent = Agent.register(
                id,
                request.getName(),
                request.getDescription(),
                request.getEndpoint(),
                request.getVersion(),
                request.getType()
        );

        // 保存
        Agent saved = agentRepository.save(agent);

        // 发布领域事件
        eventPublisher.publishAll(agent.pullDomainEvents());

        log.info("Agent 注册成功: id={}, name={}", id, request.getName());
        return toDTO(saved);
    }

    @Override
    @Transactional
    public AgentDTO update(AgentUpdateRequest request) {
        Agent agent = agentRepository.findById(request.getId());
        if (agent == null) {
            throw new AgentNotFoundException(request.getId());
        }

        // 检查新名称是否已被使用
        if (request.getName() != null && !request.getName().equals(agent.getName())) {
            if (agentRepository.existsByName(request.getName())) {
                throw new AgentAlreadyExistsException(request.getName());
            }
        }

        // 更新
        agent.update(
                request.getName(),
                request.getDescription(),
                request.getEndpoint(),
                request.getVersion(),
                request.getType()
        );

        Agent updated = agentRepository.update(agent);
        log.info("Agent 更新成功: id={}", request.getId());
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void unregister(Long id) {
        Agent agent = agentRepository.findById(id);
        if (agent == null) {
            throw new AgentNotFoundException(id);
        }

        agent.delete();
        eventPublisher.publishAll(agent.pullDomainEvents());

        agentRepository.deleteById(id);
        log.info("Agent 注销成功: id={}", id);
    }

    @Override
    @Transactional
    public AgentDTO activate(Long id) {
        Agent agent = agentRepository.findById(id);
        if (agent == null) {
            throw new AgentNotFoundException(id);
        }

        agent.activate();
        eventPublisher.publishAll(agent.pullDomainEvents());

        Agent updated = agentRepository.update(agent);
        log.info("Agent 激活成功: id={}", id);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public AgentDTO deactivate(Long id) {
        Agent agent = agentRepository.findById(id);
        if (agent == null) {
            throw new AgentNotFoundException(id);
        }

        agent.deactivate();
        eventPublisher.publishAll(agent.pullDomainEvents());

        Agent updated = agentRepository.update(agent);
        log.info("Agent 停用成功: id={}", id);
        return toDTO(updated);
    }

    // ==================== 发现功能 ====================

    @Override
    public AgentDTO findById(Long id) {
        AgentInfo agentInfo = agentRepository.findInfoById(id);
        if (agentInfo == null) {
            throw new AgentNotFoundException(id);
        }
        return toDTO(agentInfo);
    }

    @Override
    public AgentDTO findByName(String name) {
        AgentInfo agentInfo = agentRepository.findInfoByName(name);
        if (agentInfo == null) {
            throw new AgentNotFoundException(name);
        }
        return toDTO(agentInfo);
    }

    @Override
    public AgentListResponse findAll() {
        List<AgentInfo> agents = agentRepository.findAllInfo();
        return AgentListResponse.builder()
                .agents(agents.stream().map(this::toDTO).collect(Collectors.toList()))
                .total((long) agents.size())
                .pageNum(1)
                .pageSize(agents.size())
                .hasMore(false)
                .build();
    }

    @Override
    public AgentListResponse findByStatus(String status) {
        AgentStatus agentStatus = AgentStatus.valueOf(status);
        List<AgentInfo> agents = agentRepository.findInfoByStatus(agentStatus);
        return AgentListResponse.builder()
                .agents(agents.stream().map(this::toDTO).collect(Collectors.toList()))
                .total((long) agents.size())
                .pageNum(1)
                .pageSize(agents.size())
                .hasMore(false)
                .build();
    }

    @Override
    public AgentListResponse query(AgentQueryRequest request) {
        List<AgentInfo> agents;

        if (request.getName() != null && !request.getName().isEmpty()) {
            agents = agentRepository.findByNameLike(request.getName());
        } else if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            AgentStatus status = AgentStatus.valueOf(request.getStatus());
            agents = agentRepository.findInfoByStatus(status);
        } else {
            agents = agentRepository.findAllInfo();
        }

        long total = agents.size();

        // 分页
        int start = (request.getPageNum() - 1) * request.getPageSize();
        int end = Math.min(start + request.getPageSize(), agents.size());
        List<AgentInfo> pagedAgents = start < agents.size() ?
                agents.subList(start, end) : List.of();

        return AgentListResponse.builder()
                .agents(pagedAgents.stream().map(this::toDTO).collect(Collectors.toList()))
                .total(total)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .hasMore((long) request.getPageNum() * request.getPageSize() < total)
                .build();
    }

    @Override
    public long count() {
        return agentRepository.count();
    }

    @Override
    public long countByStatus(String status) {
        AgentStatus agentStatus = AgentStatus.valueOf(status);
        return agentRepository.countByStatus(agentStatus);
    }
}