package com.agenthub.discovery.service;

import com.agenthub.common.dto.AgentDTO;
import com.agenthub.discovery.dto.AgentQueryRequest;

import java.util.List;

/**
 * Agent Discovery Service Interface
 */
public interface AgentDiscoveryService {

    /**
     * Find agent by ID
     */
    AgentDTO findById(Long id);

    /**
     * Find all agents
     */
    List<AgentDTO> findAll();

    /**
     * Find agents by status
     */
    List<AgentDTO> findByStatus(String status);

    /**
     * Query agents with conditions
     */
    List<AgentDTO> query(AgentQueryRequest request);

    /**
     * Check if agent exists
     */
    boolean existsById(Long id);
}
