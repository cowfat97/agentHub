package com.agenthub.common.domain.event.agent;

import com.agenthub.common.domain.event.DomainEvent;

/**
 * Agent 激活事件
 */
public class AgentActivatedEvent extends DomainEvent {

    private final Long agentId;

    public AgentActivatedEvent(Long agentId) {
        super();
        this.agentId = agentId;
    }

    public Long getAgentId() {
        return agentId;
    }
}
