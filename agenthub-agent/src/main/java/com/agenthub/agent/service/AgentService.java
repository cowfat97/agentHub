package com.agenthub.agent.service;

import com.agenthub.agent.dto.AgentListResponse;
import com.agenthub.agent.dto.AgentQueryRequest;
import com.agenthub.agent.dto.AgentRegisterRequest;
import com.agenthub.agent.dto.AgentUpdateRequest;
import com.agenthub.common.dto.AgentDTO;

/**
 * Agent 服务接口
 *
 * 统一管理 Agent 的注册和发现功能
 */
public interface AgentService {

    // ==================== 注册功能 ====================

    /**
     * 注册 Agent
     */
    AgentDTO register(AgentRegisterRequest request);

    /**
     * 更新 Agent
     */
    AgentDTO update(AgentUpdateRequest request);

    /**
     * 注销 Agent
     */
    void unregister(Long id);

    /**
     * 激活 Agent
     */
    AgentDTO activate(Long id);

    /**
     * 停用 Agent
     */
    AgentDTO deactivate(Long id);

    // ==================== 发现功能 ====================

    /**
     * 根据ID查询
     */
    AgentDTO findById(Long id);

    /**
     * 根据名称查询
     */
    AgentDTO findByName(String name);

    /**
     * 查询所有
     */
    AgentListResponse findAll();

    /**
     * 根据状态查询
     */
    AgentListResponse findByStatus(String status);

    /**
     * 条件查询
     */
    AgentListResponse query(AgentQueryRequest request);

    /**
     * 统计总数
     */
    long count();

    /**
     * 根据状态统计
     */
    long countByStatus(String status);
}