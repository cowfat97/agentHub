package com.agenthub.registration.domain.entity;

import com.agenthub.common.domain.event.DomainEvent;
import com.agenthub.common.domain.event.agent.AgentActivatedEvent;
import com.agenthub.common.domain.event.agent.AgentDeactivatedEvent;
import com.agenthub.common.domain.event.agent.AgentDeletedEvent;
import com.agenthub.common.domain.event.agent.AgentRegisteredEvent;
import com.agenthub.common.domain.exception.AgentAlreadyDeletedException;
import com.agenthub.common.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent 聚合根 - 领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {

    /**
     * Agent 唯一标识
     */
    private Long id;

    /**
     * Agent 名称（全局唯一）
     */
    private String name;

    /**
     * Agent 描述
     */
    private String description;

    /**
     * Agent 服务端点
     */
    private String endpoint;

    /**
     * Agent 版本
     */
    private String version;

    /**
     * Agent 状态
     */
    private AgentStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 领域事件列表
     */
    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 获取并清空领域事件
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * 注册新 Agent
     */
    public static Agent register(Long id, String name, String description, String endpoint, String version) {
        Agent agent = Agent.builder()
                .id(id)
                .name(name)
                .description(description)
                .endpoint(endpoint)
                .version(version)
                .status(AgentStatus.ONLINE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 添加注册事件
        agent.domainEvents.add(new AgentRegisteredEvent(id, name, description, endpoint, version));
        
        return agent;
    }

    /**
     * 更新 Agent 信息
     */
    public void update(String name, String description, String endpoint, String version) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (endpoint != null) {
            this.endpoint = endpoint;
        }
        if (version != null) {
            this.version = version;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活 Agent (上线)
     */
    public void activate() {
        if (this.status == AgentStatus.OFFLINE) {
            throw new AgentAlreadyDeletedException(this.id);
        }
        this.status = AgentStatus.ONLINE;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new AgentActivatedEvent(this.id));
    }

    /**
     * 停用 Agent (离线)
     */
    public void deactivate() {
        if (this.status == AgentStatus.OFFLINE) {
            throw new AgentAlreadyDeletedException(this.id);
        }
        this.status = AgentStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new AgentDeactivatedEvent(this.id));
    }

    /**
     * 删除 Agent
     */
    public void delete() {
        if (this.status == AgentStatus.OFFLINE) {
            throw new AgentAlreadyDeletedException(this.id);
        }
        this.status = AgentStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
        this.domainEvents.add(new AgentDeletedEvent(this.id));
    }

    /**
     * 离线（兼容旧代码）
     */
    public void offline() {
        deactivate();
    }

    /**
     * 上线（兼容旧代码）
     */
    public void online() {
        activate();
    }

    /**
     * 是否活跃
     */
    public boolean isActive() {
        return this.status == AgentStatus.ONLINE;
    }
}
