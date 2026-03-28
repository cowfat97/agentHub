package com.agenthub.common.domain.exception;

/**
 * Agent 名称格式无效异常
 */
public class InvalidAgentNameException extends DomainException {

    public InvalidAgentNameException(String name) {
        super("Agent name 格式无效: " + name, "INVALID_AGENT_NAME");
    }
}
