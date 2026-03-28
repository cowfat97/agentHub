package com.agenthub.registration.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Agent Update Request DTO
 */
@Data
public class AgentUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent ID
     */
    private Long id;

    /**
     * Agent Name
     */
    private String name;

    /**
     * Agent Description
     */
    private String description;

    /**
     * Agent Type
     */
    private String type;

    /**
     * Agent Endpoint URL
     */
    private String endpoint;

    /**
     * Agent Status
     */
    private String status;

    /**
     * Agent Metadata (JSON String)
     */
    private String metadata;
}