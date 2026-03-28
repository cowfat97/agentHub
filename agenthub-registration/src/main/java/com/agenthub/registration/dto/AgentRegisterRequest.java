package com.agenthub.registration.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Agent Register Request DTO
 */
@Data
public class AgentRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * Agent Metadata (JSON String)
     */
    private String metadata;
}