package com.agenthub.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent 持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPO {

    private Long id;
    private String name;
    private String description;
    private String endpoint;
    private String version;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
