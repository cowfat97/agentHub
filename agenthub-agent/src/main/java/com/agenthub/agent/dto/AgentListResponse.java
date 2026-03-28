package com.agenthub.agent.dto;

import com.agenthub.common.dto.AgentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent 列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentListResponse {

    /**
     * Agent 列表
     */
    private List<AgentDTO> agents;

    /**
     * 总数
     */
    private Long total;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 是否有更多
     */
    private Boolean hasMore;
}