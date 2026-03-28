package com.agenthub.registration.domain.repository;

import com.agenthub.registration.domain.entity.Agent;

import java.util.List;

/**
 * Agent 仓储接口（领域层定义，基础设施层实现）
 */
public interface AgentRegistrationRepository {

    /**
     * 保存 Agent
     */
    Agent save(Agent agent);

    /**
     * 更新 Agent
     */
    Agent update(Agent agent);

    /**
     * 根据 ID 查询
     */
    Agent findById(Long id);

    /**
     * 根据名称查询
     */
    Agent findByName(String name);

    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 删除 Agent
     */
    void deleteById(Long id);
}