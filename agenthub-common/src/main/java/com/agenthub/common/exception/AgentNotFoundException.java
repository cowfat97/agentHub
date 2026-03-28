package com.agenthub.common.exception;

/**
 * Agent Not Found Exception
 */
public class AgentNotFoundException extends AgentHubException {

    public AgentNotFoundException(String message) {
        super("AGENT_NOT_FOUND", message);
    }

    public AgentNotFoundException(Long agentId) {
        super("AGENT_NOT_FOUND", "Agent not found with id: " + agentId);
    }
}