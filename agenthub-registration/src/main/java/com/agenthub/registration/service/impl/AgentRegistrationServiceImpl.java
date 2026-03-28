package com.agenthub.registration.service.impl;

import com.agenthub.common.domain.event.DomainEventPublisher;
import com.agenthub.common.domain.exception.AgentAlreadyExistsException;
import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.registration.domain.entity.Agent;
import com.agenthub.registration.dto.AgentRegisterRequest;
import com.agenthub.registration.dto.AgentUpdateRequest;
import com.agenthub.registration.service.AgentRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Agent Registration Service Implementation
 * MVP v1.0.1 - 使用内存存储，后续替换为数据库
 */
@Service
@RequiredArgsConstructor
public class AgentRegistrationServiceImpl implements AgentRegistrationService {

    // 内存存储 (MVP)
    private final Map<Long, Agent> agentStore = new HashMap<>();
    private final Map<String, Long> nameIndex = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    private final DomainEventPublisher eventPublisher;

    @Override
    public AgentDTO register(AgentRegisterRequest request) {
        // 检查名称是否已存在
        if (nameIndex.containsKey(request.getName())) {
            throw new AgentAlreadyExistsException(request.getName());
        }

        // 创建领域实体（会自动添加注册事件）
        Long id = idGenerator.getAndIncrement();
        Agent agent = Agent.register(
            id,
            request.getName(),
            request.getDescription(),
            request.getEndpoint(),
            request.getType()  // 使用 type 作为 version
        );

        // 存储
        agentStore.put(id, agent);
        nameIndex.put(request.getName(), id);

        // 发布领域事件
        eventPublisher.publishAll(agent.pullDomainEvents());

        return toDTO(agent);
    }

    @Override
    public AgentDTO update(AgentUpdateRequest request) {
        Agent agent = findByIdEntity(request.getId());

        // 检查新名称是否已被使用
        if (request.getName() != null && !request.getName().equals(agent.getName())) {
            if (nameIndex.containsKey(request.getName())) {
                throw new AgentAlreadyExistsException(request.getName());
            }
            nameIndex.remove(agent.getName());
            nameIndex.put(request.getName(), request.getId());
        }

        // 调用领域实体的更新方法
        agent.update(
            request.getName(),
            request.getDescription(),
            request.getEndpoint(),
            request.getType()  // 使用 type 作为 version
        );

        return toDTO(agent);
    }

    @Override
    public void unregister(Long id) {
        Agent agent = findByIdEntity(id);
        agent.delete();
        
        // 发布领域事件
        eventPublisher.publishAll(agent.pullDomainEvents());
        
        agentStore.remove(id);
        nameIndex.remove(agent.getName());
    }

    private Agent findByIdEntity(Long id) {
        Agent agent = agentStore.get(id);
        if (agent == null) {
            throw new AgentNotFoundException(id);
        }
        return agent;
    }

    private AgentDTO toDTO(Agent agent) {
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
