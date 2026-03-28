package com.agenthub.common.domain.event.agent;

import com.agenthub.common.domain.event.DomainEvent;

/**
 * Agent 删除事件
 */
public class AgentDeletedEvent extends DomainEvent {

    private final Long agentId;

    public AgentDeletedEvent(Long agentId) {
        super();
        this.agentId = agentId;
    }

    public Long getAgentId() {
        return agentId;
    }
}
