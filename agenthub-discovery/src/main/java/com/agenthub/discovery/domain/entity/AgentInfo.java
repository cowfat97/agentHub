package com.agenthub.discovery.domain.entity;

import com.agenthub.common.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent 聚合根 - 发现领域
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfo {

    /**
     * Agent 唯一标识
     */
    private Long id;

    /**
     * Agent 名称
     */
    private String name;

    /**
     * Agent 描述
     */
    private String description;

    /**
     * Agent 服务端点
     */
    private String endpoint;

    /**
     * Agent 版本
     */
    private String version;

    /**
     * Agent 状态
     */
    private AgentStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}