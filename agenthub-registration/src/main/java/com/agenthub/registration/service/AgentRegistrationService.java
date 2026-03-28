package com.agenthub.registration.service;

import com.agenthub.common.dto.AgentDTO;
import com.agenthub.registration.dto.AgentRegisterRequest;
import com.agenthub.registration.dto.AgentUpdateRequest;

/**
 * Agent Registration Service Interface
 */
public interface AgentRegistrationService {

    /**
     * Register a new agent
     */
    AgentDTO register(AgentRegisterRequest request);

    /**
     * Update an existing agent
     */
    AgentDTO update(AgentUpdateRequest request);

    /**
     * Unregister an agent
     */
    void unregister(Long agentId);
}
