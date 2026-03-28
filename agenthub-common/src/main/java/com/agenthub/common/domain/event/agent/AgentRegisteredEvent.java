package com.agenthub.common.domain.event.agent;

import com.agenthub.common.domain.event.DomainEvent;

/**
 * Agent 注册事件
 */
public class AgentRegisteredEvent extends DomainEvent {

    private final Long agentId;
    private final String name;
    private final String description;
    private final String endpoint;
    private final String version;

    public AgentRegisteredEvent(Long agentId, String name, String description, 
                                 String endpoint, String version) {
        super();
        this.agentId = agentId;
        this.name = name;
        this.description = description;
        this.endpoint = endpoint;
        this.version = version;
    }

    public Long getAgentId() {
        return agentId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getVersion() {
        return version;
    }
}
