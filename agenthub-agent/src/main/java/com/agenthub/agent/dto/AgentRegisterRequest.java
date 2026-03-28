package com.agenthub.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Agent 注册请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRegisterRequest {

    @NotBlank(message = "Agent名称不能为空")
    private String name;

    private String description;

    private String endpoint;

    private String version;

    private String type;
}