package com.agenthub.common.exception;

/**
 * Agent Already Exists Exception
 */
public class AgentAlreadyExistsException extends AgentHubException {

    public AgentAlreadyExistsException(String message) {
        super("AGENT_ALREADY_EXISTS", message);
    }

    public AgentAlreadyExistsException(String agentName, String field) {
        super("AGENT_ALREADY_EXISTS", 
              "Agent already exists with " + field + ": " + agentName);
    }
}