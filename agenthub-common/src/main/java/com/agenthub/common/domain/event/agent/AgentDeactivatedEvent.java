package com.agenthub.common.domain.event.agent;

import com.agenthub.common.domain.event.DomainEvent;

/**
 * Agent 停用事件
 */
public class AgentDeactivatedEvent extends DomainEvent {

    private final Long agentId;

    public AgentDeactivatedEvent(Long agentId) {
        super();
        this.agentId = agentId;
    }

    public Long getAgentId() {
        return agentId;
    }
}
