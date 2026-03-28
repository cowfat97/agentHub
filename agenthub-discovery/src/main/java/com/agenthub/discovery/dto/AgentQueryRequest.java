package com.agenthub.discovery.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Agent Query Request DTO
 */
@Data
public class AgentQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent Name (fuzzy match)
     */
    private String name;

    /**
     * Agent Type
     */
    private String type;

    /**
     * Agent Status
     */
    private String status;

    /**
     * Page Number (starting from 1)
     */
    private Integer pageNum = 1;

    /**
     * Page Size
     */
    private Integer pageSize = 10;
}