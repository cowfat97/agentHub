package com.agenthub.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 大模型调用日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMCallLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 来源模块
     */
    private String sourceModule;

    /**
     * 关联业务ID
     */
    private Long businessId;

    /**
     * 请求内容（截取前1000字符）
     */
    private String requestContent;

    /**
     * 响应内容（截取前2000字符）
     */
    private String responseContent;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * Prompt Token数
     */
    private Integer promptTokens;

    /**
     * Completion Token数
     */
    private Integer completionTokens;

    /**
     * 总Token数
     */
    private Integer totalTokens;

    /**
     * 调用耗时（毫秒）
     */
    private Long duration;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}