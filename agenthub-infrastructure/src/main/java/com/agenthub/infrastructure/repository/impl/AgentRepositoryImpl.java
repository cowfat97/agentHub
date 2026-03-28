package com.agenthub.infrastructure.repository.impl;

import com.agenthub.agent.domain.entity.Agent;
import com.agenthub.agent.domain.repository.AgentRepository;
import com.agenthub.agent.domain.valueobject.AgentInfo;
import com.agenthub.common.enums.AgentStatus;
import com.agenthub.common.utils.SnowflakeIdGenerator;
import com.agenthub.infrastructure.entity.AgentPO;
import com.agenthub.infrastructure.mapper.AgentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent 仓储实现
 *
 * 统一实现注册和发现功能的持久化操作
 */
@Repository
@RequiredArgsConstructor
public class AgentRepositoryImpl implements AgentRepository {

    private final AgentMapper agentMapper;
    private final SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();

    // ==================== PO 转换 ====================

    private Agent toAgent(AgentPO po) {
        if (po == null) {
            return null;
        }
        return Agent.builder()
                .id(po.getId())
                .name(po.getName())
                .description(po.getDescription())
                .endpoint(po.getEndpoint())
                .version(po.getVersion())
                .type(po.getType())
                .status(AgentStatus.valueOf(po.getStatus()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private AgentInfo toAgentInfo(AgentPO po) {
        if (po == null) {
            return null;
        }
        return AgentInfo.builder()
                .id(po.getId())
                .name(po.getName())
                .description(po.getDescription())
                .endpoint(po.getEndpoint())
                .version(po.getVersion())
                .type(po.getType())
                .status(AgentStatus.valueOf(po.getStatus()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private AgentPO toAgentPO(Agent agent) {
        if (agent == null) {
            return null;
        }
        return AgentPO.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .endpoint(agent.getEndpoint())
                .version(agent.getVersion())
                .type(agent.getType())
                .status(agent.getStatus() != null ? agent.getStatus().name() : AgentStatus.ONLINE.name())
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }

    // ==================== 写操作 ====================

    @Override
    public Agent save(Agent agent) {
        AgentPO po = toAgentPO(agent);
        if (po.getCreatedAt() == null) {
            po.setCreatedAt(LocalDateTime.now());
        }
        if (po.getUpdatedAt() == null) {
            po.setUpdatedAt(LocalDateTime.now());
        }
        agentMapper.insert(po);
        return toAgent(po);
    }

    @Override
    public Agent update(Agent agent) {
        AgentPO po = toAgentPO(agent);
        po.setUpdatedAt(LocalDateTime.now());
        agentMapper.update(po);
        return toAgent(po);
    }

    @Override
    public void deleteById(Long id) {
        agentMapper.deleteById(id);
    }

    @Override
    public Long nextId() {
        return idGenerator.nextId();
    }

    // ==================== 读操作 ====================

    @Override
    public Agent findById(Long id) {
        AgentPO po = agentMapper.selectById(id);
        return toAgent(po);
    }

    @Override
    public AgentInfo findInfoById(Long id) {
        AgentPO po = agentMapper.selectById(id);
        return toAgentInfo(po);
    }

    @Override
    public Agent findByName(String name) {
        AgentPO po = agentMapper.selectByName(name);
        return toAgent(po);
    }

    @Override
    public AgentInfo findInfoByName(String name) {
        AgentPO po = agentMapper.selectByName(name);
        return toAgentInfo(po);
    }

    @Override
    public List<Agent> findAll() {
        List<AgentPO> pos = agentMapper.selectAll();
        return pos.stream()
                .map(this::toAgent)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentInfo> findAllInfo() {
        List<AgentPO> pos = agentMapper.selectAll();
        return pos.stream()
                .map(this::toAgentInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Agent> findByStatus(AgentStatus status) {
        List<AgentPO> pos = agentMapper.selectByStatus(status.name());
        return pos.stream()
                .map(this::toAgent)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentInfo> findInfoByStatus(AgentStatus status) {
        List<AgentPO> pos = agentMapper.selectByStatus(status.name());
        return pos.stream()
                .map(this::toAgentInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentInfo> findByNameLike(String name) {
        List<AgentPO> pos = agentMapper.selectByNameLike(name);
        return pos.stream()
                .map(this::toAgentInfo)
                .collect(Collectors.toList());
    }

    // ==================== 校验操作 ====================

    @Override
    public boolean existsByName(String name) {
        return agentMapper.selectByName(name) != null;
    }

    @Override
    public long count() {
        return agentMapper.count();
    }

    @Override
    public long countByStatus(AgentStatus status) {
        return agentMapper.countByStatus(status.name());
    }
}