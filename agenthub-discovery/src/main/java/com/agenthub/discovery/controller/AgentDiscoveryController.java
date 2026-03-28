package com.agenthub.discovery.controller;

import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.dto.AgentDTO;
import com.agenthub.common.result.ApiResponse;
import com.agenthub.discovery.dto.AgentQueryRequest;
import com.agenthub.discovery.service.AgentDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Agent Discovery Controller
 */
@RestController
@RequestMapping("/api/v1/discovery")
@RequiredArgsConstructor
public class AgentDiscoveryController {

    private final AgentDiscoveryService discoveryService;

    /**
     * Get agent by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<AgentDTO> getById(@PathVariable Long id) {
        AgentDTO agent = discoveryService.findById(id);
        if (agent == null) {
            throw new AgentNotFoundException(id);
        }
        return ApiResponse.success(agent);
    }

    /**
     * List all agents
     */
    @GetMapping
    public ApiResponse<List<AgentDTO>> list() {
        List<AgentDTO> agents = discoveryService.findAll();
        return ApiResponse.success(agents);
    }

    /**
     * List agents by status
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<AgentDTO>> listByStatus(@PathVariable String status) {
        List<AgentDTO> agents = discoveryService.findByStatus(status);
        return ApiResponse.success(agents);
    }

    /**
     * Query agents with conditions
     */
    @PostMapping("/query")
    public ApiResponse<List<AgentDTO>> query(@RequestBody AgentQueryRequest request) {
        List<AgentDTO> agents = discoveryService.query(request);
        return ApiResponse.success(agents);
    }

    /**
     * Check if agent exists
     */
    @GetMapping("/{id}/exists")
    public ApiResponse<Boolean> exists(@PathVariable Long id) {
        return ApiResponse.success(discoveryService.existsById(id));
    }
}
