package com.agenthub.discovery.service.impl;

import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.discovery.domain.entity.AgentInfo;
import com.agenthub.discovery.dto.AgentQueryRequest;
import com.agenthub.discovery.service.AgentDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Agent Discovery Service Implementation
 * MVP v1.0.1 - 使用内存存储
 */
@Service
@RequiredArgsConstructor
public class AgentDiscoveryServiceImpl implements AgentDiscoveryService {

    // 内存存储 (从 registration 模块同步，MVP 阶段)
    private static final Map<Long, AgentInfo> agentStore = new ConcurrentHashMap<>();

    @Override
    public AgentDTO findById(Long id) {
        AgentInfo agent = agentStore.get(id);
        if (agent == null) {
            throw new AgentNotFoundException(id);
        }
        return toDTO(agent);
    }

    @Override
    public List<AgentDTO> findAll() {
        return agentStore.values().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> findByStatus(String status) {
        return agentStore.values().stream()
                .filter(a -> status.equals(a.getStatus() != null ? a.getStatus().name() : null))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> query(AgentQueryRequest request) {
        return agentStore.values().stream()
                .filter(a -> request.getName() == null || a.getName().contains(request.getName()))
                .filter(a -> request.getStatus() == null || request.getStatus().equals(a.getStatus() != null ? a.getStatus().name() : null))
                .skip((long) (request.getPageNum() - 1) * request.getPageSize())
                .limit(request.getPageSize())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return agentStore.containsKey(id);
    }

    private AgentDTO toDTO(AgentInfo agent) {
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
}
