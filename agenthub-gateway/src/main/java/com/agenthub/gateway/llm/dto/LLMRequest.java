package com.agenthub.gateway.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 大模型请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求类型
     * - CONTENT_REVIEW: 内容审核
     * - TAG_EXTRACTION: 标签提取
     * - CLASSIFICATION: 分类建议
     * - SUMMARIZATION: 摘要生成
     */
    private String requestType;

    /**
     * 输入内容
     */
    private String content;

    /**
     * 标题（可选，用于上下文）
     */
    private String title;

    /**
     * 额外参数
     */
    private Map<String, Object> params;

    /**
     * 请求来源模块
     */
    private String sourceModule;

    /**
     * 关联的业务ID（用于日志追踪）
     */
    private Long businessId;
}