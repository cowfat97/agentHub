package com.agenthub.discovery.domain.repository;

import com.agenthub.common.enums.AgentStatus;
import com.agenthub.discovery.domain.entity.AgentInfo;

import java.util.List;

/**
 * Agent 发现仓储接口（领域层定义，基础设施层实现）
 */
public interface AgentDiscoveryRepository {

    /**
     * 根据ID查询Agent
     */
    AgentInfo findAgentInfoById(Long id);

    /**
     * 根据名称查询Agent
     */
    AgentInfo findAgentInfoByName(String name);

    /**
     * 查询所有Agent
     */
    List<AgentInfo> findAll();

    /**
     * 根据状态查询Agent列表
     */
    List<AgentInfo> findByStatus(AgentStatus status);

    /**
     * 根据名称模糊查询
     */
    List<AgentInfo> findByNameLike(String name);

    /**
     * 统计Agent总数
     */
    long count();

    /**
     * 根据状态统计Agent数量
     */
    long countByStatus(AgentStatus status);
}