package com.agenthub.agent.controller;

import com.agenthub.agent.dto.*;
import com.agenthub.agent.service.AgentService;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Agent 统一控制器
 *
 * 整合注册和发现 API
 */
@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    // ==================== 注册 API ====================

    /**
     * 注册 Agent
     */
    @PostMapping
    public ApiResponse<AgentDTO> register(@RequestBody AgentRegisterRequest request) {
        AgentDTO agent = agentService.register(request);
        return ApiResponse.success(agent);
    }

    /**
     * 更新 Agent
     */
    @PutMapping("/{id}")
    public ApiResponse<AgentDTO> update(@PathVariable Long id,
                                        @RequestBody AgentUpdateRequest request) {
        request.setId(id);
        AgentDTO agent = agentService.update(request);
        return ApiResponse.success(agent);
    }

    /**
     * 注销 Agent
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> unregister(@PathVariable Long id) {
        agentService.unregister(id);
        return ApiResponse.success();
    }

    /**
     * 激活 Agent
     */
    @PostMapping("/{id}/activate")
    public ApiResponse<AgentDTO> activate(@PathVariable Long id) {
        AgentDTO agent = agentService.activate(id);
        return ApiResponse.success(agent);
    }

    /**
     * 停用 Agent
     */
    @PostMapping("/{id}/deactivate")
    public ApiResponse<AgentDTO> deactivate(@PathVariable Long id) {
        AgentDTO agent = agentService.deactivate(id);
        return ApiResponse.success(agent);
    }

    // ==================== 发现 API ====================

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public ApiResponse<AgentDTO> findById(@PathVariable Long id) {
        AgentDTO agent = agentService.findById(id);
        return ApiResponse.success(agent);
    }

    /**
     * 根据名称查询
     */
    @GetMapping("/name/{name}")
    public ApiResponse<AgentDTO> findByName(@PathVariable String name) {
        AgentDTO agent = agentService.findByName(name);
        return ApiResponse.success(agent);
    }

    /**
     * 查询所有
     */
    @GetMapping
    public ApiResponse<AgentListResponse> findAll() {
        AgentListResponse response = agentService.findAll();
        return ApiResponse.success(response);
    }

    /**
     * 根据状态查询
     */
    @GetMapping("/status/{status}")
    public ApiResponse<AgentListResponse> findByStatus(@PathVariable String status) {
        AgentListResponse response = agentService.findByStatus(status);
        return ApiResponse.success(response);
    }

    /**
     * 条件查询
     */
    @GetMapping("/query")
    public ApiResponse<AgentListResponse> query(AgentQueryRequest request) {
        AgentListResponse response = agentService.query(request);
        return ApiResponse.success(response);
    }

    /**
     * 统计总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> count() {
        long count = agentService.count();
        return ApiResponse.success(count);
    }

    /**
     * 根据状态统计
     */
    @GetMapping("/count/{status}")
    public ApiResponse<Long> countByStatus(@PathVariable String status) {
        long count = agentService.countByStatus(status);
        return ApiResponse.success(count);
    }
}