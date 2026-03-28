package com.agenthub.agent.domain.valueobject;

import com.agenthub.agent.domain.entity.Agent;
import com.agenthub.common.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent 查询视图对象
 *
 * 用于发现模块的查询场景，不包含敏感信息
 * 从 Agent 实体转换而来
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
     * Agent 类型
     */
    private String type;

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

    /**
     * 从 Agent 实体转换
     */
    public static AgentInfo from(Agent agent) {
        if (agent == null) {
            return null;
        }
        return AgentInfo.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .endpoint(agent.getEndpoint())
                .version(agent.getVersion())
                .type(agent.getType())
                .status(agent.getStatus())
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }
}