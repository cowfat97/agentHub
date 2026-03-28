package com.agenthub.agent.domain.repository;

import com.agenthub.agent.domain.entity.Agent;
import com.agenthub.agent.domain.valueobject.AgentInfo;
import com.agenthub.common.enums.AgentStatus;

import java.util.List;

/**
 * Agent 仓储接口
 *
 * 统一管理 Agent 的持久化操作
 * 合并了原 AgentRegistrationRepository 和 AgentDiscoveryRepository
 */
public interface AgentRepository {

    // ==================== 写操作（注册领域）====================

    /**
     * 保存 Agent
     */
    Agent save(Agent agent);

    /**
     * 更新 Agent
     */
    Agent update(Agent agent);

    /**
     * 删除 Agent
     */
    void deleteById(Long id);

    /**
     * 获取下一个ID
     */
    Long nextId();

    // ==================== 读操作（发现领域）====================

    /**
     * 根据ID查询 Agent
     */
    Agent findById(Long id);

    /**
     * 根据ID查询 AgentInfo
     */
    AgentInfo findInfoById(Long id);

    /**
     * 根据名称查询 Agent
     */
    Agent findByName(String name);

    /**
     * 根据名称查询 AgentInfo
     */
    AgentInfo findInfoByName(String name);

    /**
     * 查询所有 Agent
     */
    List<Agent> findAll();

    /**
     * 查询所有 AgentInfo
     */
    List<AgentInfo> findAllInfo();

    /**
     * 根据状态查询 Agent
     */
    List<Agent> findByStatus(AgentStatus status);

    /**
     * 根据状态查询 AgentInfo
     */
    List<AgentInfo> findInfoByStatus(AgentStatus status);

    /**
     * 根据名称模糊查询
     */
    List<AgentInfo> findByNameLike(String name);

    // ==================== 校验操作 ====================

    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 统计总数
     */
    long count();

    /**
     * 根据状态统计
     */
    long countByStatus(AgentStatus status);
}