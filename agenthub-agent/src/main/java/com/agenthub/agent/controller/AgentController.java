package com.agenthub.agent.controller;

import com.agenthub.agent.dto.*;
import com.agenthub.agent.service.AgentService;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.result.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Agent 统一控制器
 *
 * 整合注册和发现 API
 */
@Tag(name = "Agent", description = "Agent 注册与发现 API")
@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    // ==================== 注册 API ====================

    /**
     * 注册 Agent
     */
    @Operation(summary = "注册 Agent", description = "将新 Agent 注册到平台")
    @PostMapping
    public ApiResponse<AgentDTO> register(@RequestBody AgentRegisterRequest request) {
        AgentDTO agent = agentService.register(request);
        return ApiResponse.success(agent);
    }

    /**
     * 更新 Agent
     */
    @Operation(summary = "更新 Agent", description = "更新 Agent 信息")
    @PutMapping("/{id}")
    public ApiResponse<AgentDTO> update(@Parameter(description = "Agent ID") @PathVariable Long id,
                                        @RequestBody AgentUpdateRequest request) {
        request.setId(id);
        AgentDTO agent = agentService.update(request);
        return ApiResponse.success(agent);
    }

    /**
     * 注销 Agent
     */
    @Operation(summary = "注销 Agent", description = "从平台注销 Agent")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> unregister(@Parameter(description = "Agent ID") @PathVariable Long id) {
        agentService.unregister(id);
        return ApiResponse.success();
    }

    /**
     * 激活 Agent
     */
    @Operation(summary = "激活 Agent", description = "将 Agent 状态改为活跃")
    @PostMapping("/{id}/activate")
    public ApiResponse<AgentDTO> activate(@Parameter(description = "Agent ID") @PathVariable Long id) {
        AgentDTO agent = agentService.activate(id);
        return ApiResponse.success(agent);
    }

    /**
     * 停用 Agent
     */
    @Operation(summary = "停用 Agent", description = "将 Agent 状态改为停用")
    @PostMapping("/{id}/deactivate")
    public ApiResponse<AgentDTO> deactivate(@Parameter(description = "Agent ID") @PathVariable Long id) {
        AgentDTO agent = agentService.deactivate(id);
        return ApiResponse.success(agent);
    }

    // ==================== 发现 API ====================

    /**
     * 根据ID查询
     */
    @Operation(summary = "根据ID查询 Agent")
    @GetMapping("/{id}")
    public ApiResponse<AgentDTO> findById(@Parameter(description = "Agent ID") @PathVariable Long id) {
        AgentDTO agent = agentService.findById(id);
        return ApiResponse.success(agent);
    }

    /**
     * 根据名称查询
     */
    @Operation(summary = "根据名称查询 Agent")
    @GetMapping("/name/{name}")
    public ApiResponse<AgentDTO> findByName(@Parameter(description = "Agent 名称") @PathVariable String name) {
        AgentDTO agent = agentService.findByName(name);
        return ApiResponse.success(agent);
    }

    /**
     * 查询所有
     */
    @Operation(summary = "查询所有 Agent")
    @GetMapping
    public ApiResponse<AgentListResponse> findAll() {
        AgentListResponse response = agentService.findAll();
        return ApiResponse.success(response);
    }

    /**
     * 根据状态查询
     */
    @Operation(summary = "根据状态查询 Agent")
    @GetMapping("/status/{status}")
    public ApiResponse<AgentListResponse> findByStatus(@Parameter(description = "状态: active/inactive") @PathVariable String status) {
        AgentListResponse response = agentService.findByStatus(status);
        return ApiResponse.success(response);
    }

    /**
     * 条件查询
     */
    @Operation(summary = "条件查询 Agent")
    @GetMapping("/query")
    public ApiResponse<AgentListResponse> query(AgentQueryRequest request) {
        AgentListResponse response = agentService.query(request);
        return ApiResponse.success(response);
    }

    /**
     * 统计总数
     */
    @Operation(summary = "统计 Agent 总数")
    @GetMapping("/count")
    public ApiResponse<Long> count() {
        long count = agentService.count();
        return ApiResponse.success(count);
    }

    /**
     * 根据状态统计
     */
    @Operation(summary = "根据状态统计 Agent 数量")
    @GetMapping("/count/{status}")
    public ApiResponse<Long> countByStatus(@Parameter(description = "状态: active/inactive") @PathVariable String status) {
        long count = agentService.countByStatus(status);
        return ApiResponse.success(count);
    }
}