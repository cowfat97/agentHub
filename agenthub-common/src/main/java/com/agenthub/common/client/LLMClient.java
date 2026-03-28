package com.agenthub.common.client;

import com.agenthub.common.result.ApiResponse;

import java.io.Serializable;
import java.util.List;

/**
 * 大模型客户端接口
 *
 * 其他模块通过此接口调用网关的大模型服务
 */
public interface LLMClient {

    /**
     * 内容审核
     */
    ApiResponse<ReviewResult> reviewContent(String content, String type);

    /**
     * 提取标签
     */
    ApiResponse<List<String>> extractTags(String title, String content);

    /**
     * 分类建议
     */
    ApiResponse<String> suggestCategory(String title, String content);

    /**
     * 生成摘要
     */
    ApiResponse<String> generateSummary(String content, int maxLength);

    /**
     * 审核结果
     */
    class ReviewResult implements Serializable {
        private Boolean approved;
        private String reason;
        private String riskLevel;
        private List<String> sensitiveWords;

        public Boolean getApproved() { return approved; }
        public void setApproved(Boolean approved) { this.approved = approved; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public List<String> getSensitiveWords() { return sensitiveWords; }
        public void setSensitiveWords(List<String> sensitiveWords) { this.sensitiveWords = sensitiveWords; }
    }
}