package com.agenthub.common.domain.exception;

/**
 * Agent 不存在异常
 */
public class AgentNotFoundException extends DomainException {

    public AgentNotFoundException(Long agentId) {
        super("Agent 不存在: " + agentId, "AGENT_NOT_FOUND");
    }

    public AgentNotFoundException(String message) {
        super(message, "AGENT_NOT_FOUND");
    }
}
