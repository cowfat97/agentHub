package com.agenthub.infrastructure.mapper;

import com.agenthub.infrastructure.entity.AgentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Agent Mapper
 */
@Mapper
public interface AgentMapper {

    /**
     * 插入Agent
     */
    int insert(AgentPO agent);

    /**
     * 更新Agent
     */
    int update(AgentPO agent);

    /**
     * 根据ID查询
     */
    AgentPO selectById(@Param("id") Long id);

    /**
     * 根据名称查询
     */
    AgentPO selectByName(@Param("name") String name);

    /**
     * 根据名称模糊查询
     */
    List<AgentPO> selectByNameLike(@Param("name") String name);

    /**
     * 根据状态查询
     */
    List<AgentPO> selectByStatus(@Param("status") String status);

    /**
     * 查询所有Agent
     */
    List<AgentPO> selectAll();

    /**
     * 统计总数
     */
    long count();

    /**
     * 根据状态统计数量
     */
    long countByStatus(@Param("status") String status);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);
}