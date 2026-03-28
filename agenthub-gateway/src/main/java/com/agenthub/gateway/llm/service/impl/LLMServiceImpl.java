package com.agenthub.gateway.llm.service.impl;

import com.agenthub.gateway.entity.LLMCallLog;
import com.agenthub.gateway.llm.dto.LLMRequest;
import com.agenthub.gateway.llm.dto.LLMResponse;
import com.agenthub.gateway.llm.service.LLMService;
import com.agenthub.gateway.mapper.LLMCallLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 大模型服务实现
 *
 * 支持配置切换不同的大模型提供商：
 * - mock: 模拟实现（默认，用于开发测试）
 * - openai: OpenAI API
 * - claude: Claude API
 * - qwen: 通义千问
 * - zhipu: 智谱AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMServiceImpl implements LLMService {

    private final LLMCallLogMapper llmCallLogMapper;

    @Value("${agenthub.llm.provider:mock}")
    private String provider;

    @Value("${agenthub.llm.model:gpt-4}")
    private String defaultModel;

    @Value("${agenthub.llm.api-key:}")
    private String apiKey;

    @Value("${agenthub.llm.api-url:}")
    private String apiUrl;

    private final AtomicLong idGenerator = new AtomicLong(1);

    // ==================== 核心调用方法 ====================

    @Override
    public LLMResponse call(LLMRequest request) {
        long startTime = System.currentTimeMillis();
        LLMResponse response;

        try {
            // 根据provider选择不同的实现
            switch (provider.toLowerCase()) {
                case "openai":
                    response = callOpenAI(request);
                    break;
                case "claude":
                    response = callClaude(request);
                    break;
                case "qwen":
                    response = callQwen(request);
                    break;
                case "zhipu":
                    response = callZhipu(request);
                    break;
                case "mock":
                default:
                    response = callMock(request);
                    break;
            }

            response.setDuration(System.currentTimeMillis() - startTime);
            response.setSuccess(true);

        } catch (Exception e) {
            log.error("大模型调用失败: provider={}, requestType={}", provider, request.getRequestType(), e);
            response = LLMResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .duration(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 异步保存调用日志
        saveCallLogAsync(request, response);

        return response;
    }

    // ==================== 各提供商实现 ====================

    /**
     * Mock实现（开发测试用）
     */
    private LLMResponse callMock(LLMRequest request) {
        log.info("Mock LLM调用: requestType={}", request.getRequestType());

        LLMResponse response = LLMResponse.builder()
                .tokenUsage(LLMResponse.TokenUsage.builder()
                        .promptTokens(100)
                        .completionTokens(50)
                        .totalTokens(150)
                        .build())
                .build();

        switch (request.getRequestType()) {
            case "CONTENT_REVIEW":
                // 简单的敏感词检测
                boolean approved = !containsSensitiveWords(request.getContent());
                response.setReviewResult(LLMResponse.ReviewResult.builder()
                        .approved(approved)
                        .reason(approved ? null : "内容包含敏感词")
                        .riskLevel(approved ? "LOW" : "MEDIUM")
                        .build());
                break;

            case "TAG_EXTRACTION":
                // 基于关键词模拟标签提取
                response.setTags(extractMockTags(request.getTitle(), request.getContent()));
                break;

            case "CLASSIFICATION":
                // 模拟分类建议
                response.setSuggestedCategory(suggestMockCategory(request.getContent()));
                break;

            case "SUMMARIZATION":
                // 截取前200字符作为摘要
                String summary = request.getContent();
                if (summary.length() > 200) {
                    summary = summary.substring(0, 200) + "...";
                }
                response.setSummary(summary);
                break;

            default:
                response.setContent("Mock response for: " + request.getRequestType());
        }

        return response;
    }

    /**
     * OpenAI实现
     */
    private LLMResponse callOpenAI(LLMRequest request) {
        // TODO: 实现OpenAI API调用
        log.info("OpenAI调用: requestType={}", request.getRequestType());
        throw new UnsupportedOperationException("OpenAI集成待实现，请配置agenthub.llm.provider=mock使用模拟模式");
    }

    /**
     * Claude实现
     */
    private LLMResponse callClaude(LLMRequest request) {
        // TODO: 实现Claude API调用
        log.info("Claude调用: requestType={}", request.getRequestType());
        throw new UnsupportedOperationException("Claude集成待实现，请配置agenthub.llm.provider=mock使用模拟模式");
    }

    /**
     * 通义千问实现
     */
    private LLMResponse callQwen(LLMRequest request) {
        // TODO: 实现通义千问API调用
        log.info("通义千问调用: requestType={}", request.getRequestType());
        throw new UnsupportedOperationException("通义千问集成待实现，请配置agenthub.llm.provider=mock使用模拟模式");
    }

    /**
     * 智谱AI实现
     */
    private LLMResponse callZhipu(LLMRequest request) {
        // TODO: 实现智谱AI API调用
        log.info("智谱AI调用: requestType={}", request.getRequestType());
        throw new UnsupportedOperationException("智谱AI集成待实现，请配置agenthub.llm.provider=mock使用模拟模式");
    }

    // ==================== 便捷方法 ====================

    @Override
    public LLMResponse.ReviewResult reviewContent(String content, String type) {
        LLMRequest request = LLMRequest.builder()
                .requestType("CONTENT_REVIEW")
                .content(content)
                .sourceModule(type + "-module")
                .build();

        LLMResponse response = call(request);
        return response.getReviewResult();
    }

    @Override
    public List<String> extractTags(String title, String content) {
        LLMRequest request = LLMRequest.builder()
                .requestType("TAG_EXTRACTION")
                .title(title)
                .content(content)
                .sourceModule("article-module")
                .build();

        LLMResponse response = call(request);
        return response.getTags();
    }

    @Override
    public String suggestCategory(String title, String content) {
        LLMRequest request = LLMRequest.builder()
                .requestType("CLASSIFICATION")
                .title(title)
                .content(content)
                .sourceModule("article-module")
                .build();

        LLMResponse response = call(request);
        return response.getSuggestedCategory();
    }

    @Override
    public String generateSummary(String content, int maxLength) {
        LLMRequest request = LLMRequest.builder()
                .requestType("SUMMARIZATION")
                .content(content)
                .sourceModule("article-module")
                .params(java.util.Collections.singletonMap("maxLength", maxLength))
                .build();

        LLMResponse response = call(request);
        return response.getSummary();
    }

    // ==================== 辅助方法 ====================

    private boolean containsSensitiveWords(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        String[] sensitiveWords = {"广告", "违规", "垃圾信息", "赌博", "诈骗"};
        for (String word : sensitiveWords) {
            if (content.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private List<String> extractMockTags(String title, String content) {
        // 基于关键词模拟标签提取
        List<String> tags = new java.util.ArrayList<>();
        String text = (title != null ? title : "") + " " + (content != null ? content : "");

        String[][] keywordTags = {
                {"代码", "代码生成", "编程", "开发"},
                {"数据", "数据分析", "统计", "报表"},
                {"AI", "人工智能", "机器学习", "深度学习"},
                {"优化", "性能优化", "效率", "加速"},
                {"教程", "教程", "指南", "入门"}
        };

        for (String[] group : keywordTags) {
            for (int i = 1; i < group.length; i++) {
                if (text.contains(group[i])) {
                    tags.add(group[0]);
                    break;
                }
            }
        }

        if (tags.isEmpty()) {
            tags.add("知识分享");
        }

        return tags.size() > 5 ? tags.subList(0, 5) : tags;
    }

    private String suggestMockCategory(String content) {
        if (content == null) {
            return "knowledge-sharing";
        }

        if (content.contains("代码") || content.contains("编程") || content.contains("开发")) {
            return "code-generation";
        }
        if (content.contains("数据") || content.contains("分析") || content.contains("统计")) {
            return "data-analysis";
        }
        if (content.contains("任务") || content.contains("规划") || content.contains("流程")) {
            return "task-planning";
        }
        if (content.contains("最佳") || content.contains("优化") || content.contains("实践")) {
            return "best-practices";
        }

        return "knowledge-sharing";
    }

    @Async
    protected void saveCallLogAsync(LLMRequest request, LLMResponse response) {
        try {
            String requestContent = request.getContent();
            if (requestContent != null && requestContent.length() > 1000) {
                requestContent = requestContent.substring(0, 1000) + "...";
            }

            String responseContent = null;
            if (response.getReviewResult() != null) {
                responseContent = response.getReviewResult().toString();
            } else if (response.getTags() != null) {
                responseContent = "Tags: " + response.getTags();
            } else if (response.getSummary() != null) {
                responseContent = response.getSummary();
            } else if (response.getSuggestedCategory() != null) {
                responseContent = "Category: " + response.getSuggestedCategory();
            } else if (response.getContent() != null) {
                responseContent = response.getContent();
            }

            if (responseContent != null && responseContent.length() > 2000) {
                responseContent = responseContent.substring(0, 2000) + "...";
            }

            LLMCallLog log = LLMCallLog.builder()
                    .id(idGenerator.getAndIncrement())
                    .requestType(request.getRequestType())
                    .sourceModule(request.getSourceModule())
                    .businessId(request.getBusinessId())
                    .requestContent(requestContent)
                    .responseContent(responseContent)
                    .success(response.getSuccess())
                    .errorMessage(response.getErrorMessage())
                    .modelName(defaultModel)
                    .promptTokens(response.getTokenUsage() != null ? response.getTokenUsage().getPromptTokens() : null)
                    .completionTokens(response.getTokenUsage() != null ? response.getTokenUsage().getCompletionTokens() : null)
                    .totalTokens(response.getTokenUsage() != null ? response.getTokenUsage().getTotalTokens() : null)
                    .duration(response.getDuration())
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            llmCallLogMapper.insert(log);
        } catch (Exception e) {
            log.error("保存大模型调用日志失败", e);
        }
    }
}