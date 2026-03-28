package com.agenthub.common.domain.exception;

/**
 * Agent 已存在异常
 */
public class AgentAlreadyExistsException extends DomainException {

    public AgentAlreadyExistsException(String name) {
        super("Agent name 已存在: " + name, "AGENT_ALREADY_EXISTS");
    }

    public AgentAlreadyExistsException(String name, String owner) {
        super("Agent name 已存在: " + name + " (owner: " + owner + ")", "AGENT_ALREADY_EXISTS");
    }
}
