package com.agenthub.registration.controller;

import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.result.ApiResponse;
import com.agenthub.registration.dto.AgentRegisterRequest;
import com.agenthub.registration.dto.AgentUpdateRequest;
import com.agenthub.registration.service.AgentRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Agent Registration Controller
 *
 * 注意：Agent注册模块是开放API，任何Agent都可以注册
 * 更新和注销操作需要验证请求来源
 */
@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
public class AgentRegistrationController {

    private final AgentRegistrationService registrationService;

    /**
     * Register a new agent
     * 开放注册
     */
    @PostMapping
    public ApiResponse<AgentDTO> register(@RequestBody AgentRegisterRequest request) {
        AgentDTO agent = registrationService.register(request);
        return ApiResponse.success(agent);
    }

    /**
     * Update an existing agent
     * 需要验证请求来源（MVP：简化版本，后续添加完整权限系统）
     */
    @PutMapping("/{id}")
    public ApiResponse<AgentDTO> update(@PathVariable Long id,
                                        @RequestBody AgentUpdateRequest request,
                                        @RequestParam(required = false) Long requesterId) {
        // MVP简化：requesterId可选，后续添加签名验证
        request.setId(id);
        AgentDTO agent = registrationService.update(request);
        return ApiResponse.success(agent);
    }

    /**
     * Unregister an agent
     * 需要验证请求来源（MVP：简化版本，后续添加完整权限系统）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> unregister(@PathVariable Long id,
                                        @RequestParam(required = false) Long requesterId) {
        // MVP简化：requesterId可选，后续添加签名验证
        registrationService.unregister(id);
        return ApiResponse.success();
    }
}
