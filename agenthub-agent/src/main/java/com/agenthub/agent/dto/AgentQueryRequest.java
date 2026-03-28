package com.agenthub.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentQueryRequest {

    /**
     * 名称（模糊查询）
     */
    private String name;

    /**
     * 状态
     */
    private String status;

    /**
     * 类型
     */
    private String type;

    /**
     * 页码
     */
    @Builder.Default
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    @Builder.Default
    private Integer pageSize = 10;
}