package com.agenthub.gateway.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 大模型响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应内容
     */
    private String content;

    /**
     * 审核结果（用于内容审核）
     */
    private ReviewResult reviewResult;

    /**
     * 提取的标签列表（用于标签提取）
     */
    private List<String> tags;

    /**
     * 建议的分类（用于分类）
     */
    private String suggestedCategory;

    /**
     * 生成的摘要
     */
    private String summary;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 模型调用耗时（毫秒）
     */
    private Long duration;

    /**
     * Token使用量
     */
    private TokenUsage tokenUsage;

    /**
     * 审核结果内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResult implements Serializable {
        /**
         * 是否通过审核
         */
        private Boolean approved;
        /**
         * 审核原因/说明
         */
        private String reason;
        /**
         * 风险等级（LOW/MEDIUM/HIGH）
         */
        private String riskLevel;
        /**
         * 命中的敏感词列表
         */
        private List<String> sensitiveWords;
    }

    /**
     * Token使用量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsage implements Serializable {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}