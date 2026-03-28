package com.agenthub.infrastructure.repository.impl;

import com.agenthub.common.enums.AgentStatus;
import com.agenthub.discovery.domain.entity.AgentInfo;
import com.agenthub.discovery.domain.repository.AgentDiscoveryRepository;
import com.agenthub.infrastructure.entity.AgentPO;
import com.agenthub.infrastructure.mapper.AgentMapper;
import com.agenthub.registration.domain.entity.Agent;
import com.agenthub.registration.domain.repository.AgentRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent Repository Implementation
 * 实现注册领域和发现领域的仓储接口
 */
@Repository
@RequiredArgsConstructor
public class AgentRepositoryImpl implements AgentRegistrationRepository, AgentDiscoveryRepository {

    private final AgentMapper agentMapper;

    // ==================== PO 转换方法 ====================

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
                .status(AgentStatus.valueOf(po.getStatus()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private AgentPO toAgentPO(Agent agent) {
        if (agent == null) {
            return null;
        }
        AgentPO po = new AgentPO();
        po.setId(agent.getId());
        po.setName(agent.getName());
        po.setDescription(agent.getDescription());
        po.setEndpoint(agent.getEndpoint());
        po.setVersion(agent.getVersion());
        po.setStatus(agent.getStatus().name());
        po.setCreatedAt(agent.getCreatedAt());
        po.setUpdatedAt(agent.getUpdatedAt());
        return po;
    }

    // ==================== AgentRegistrationRepository 实现 ====================

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
    public Agent findById(Long id) {
        AgentPO po = agentMapper.selectById(id);
        return toAgent(po);
    }

    @Override
    public Agent findByName(String name) {
        AgentPO po = agentMapper.selectByName(name);
        return toAgent(po);
    }

    @Override
    public boolean existsByName(String name) {
        return agentMapper.selectByName(name) != null;
    }

    @Override
    public void deleteById(Long id) {
        agentMapper.deleteById(id);
    }

    // ==================== AgentDiscoveryRepository 实现 ====================

    @Override
    public AgentInfo findAgentInfoById(Long id) {
        AgentPO po = agentMapper.selectById(id);
        return toAgentInfo(po);
    }

    @Override
    public AgentInfo findAgentInfoByName(String name) {
        AgentPO po = agentMapper.selectByName(name);
        return toAgentInfo(po);
    }

    @Override
    public List<AgentInfo> findAll() {
        List<AgentPO> pos = agentMapper.selectAll();
        return pos.stream()
                .map(this::toAgentInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentInfo> findByStatus(AgentStatus status) {
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

    @Override
    public long count() {
        return agentMapper.count();
    }

    @Override
    public long countByStatus(AgentStatus status) {
        return agentMapper.countByStatus(status.name());
    }
}