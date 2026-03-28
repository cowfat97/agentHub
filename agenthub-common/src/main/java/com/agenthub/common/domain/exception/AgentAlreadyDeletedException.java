package com.agenthub.common.domain.exception;

/**
 * Agent 已被删除异常
 */
public class AgentAlreadyDeletedException extends DomainException {

    public AgentAlreadyDeletedException(Long agentId) {
        super("Agent 已被删除: " + agentId, "AGENT_ALREADY_DELETED");
    }
}
