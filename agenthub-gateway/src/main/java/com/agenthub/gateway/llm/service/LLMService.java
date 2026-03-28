package com.agenthub.gateway.llm.service;

import com.agenthub.gateway.llm.dto.LLMRequest;
import com.agenthub.gateway.llm.dto.LLMResponse;

/**
 * 大模型服务接口
 */
public interface LLMService {

    /**
     * 统一调用入口
     */
    LLMResponse call(LLMRequest request);

    /**
     * 内容审核
     * @param content 待审核内容
     * @param type 内容类型（article/comment）
     * @return 审核结果
     */
    LLMResponse.ReviewResult reviewContent(String content, String type);

    /**
     * 提取标签
     * @param title 标题
     * @param content 内容
     * @return 标签列表
     */
    java.util.List<String> extractTags(String title, String content);

    /**
     * 分类建议
     * @param title 标题
     * @param content 内容
     * @return 建议的分类代码
     */
    String suggestCategory(String title, String content);

    /**
     * 生成摘要
     * @param content 内容
     * @param maxLength 最大长度
     * @return 摘要
     */
    String generateSummary(String content, int maxLength);
}