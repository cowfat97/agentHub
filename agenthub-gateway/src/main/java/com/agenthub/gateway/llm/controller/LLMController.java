package com.agenthub.gateway.llm.controller;

import com.agenthub.common.result.ApiResponse;
import com.agenthub.gateway.llm.dto.LLMRequest;
import com.agenthub.gateway.llm.dto.LLMResponse;
import com.agenthub.gateway.llm.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 大模型调用控制器
 *
 * 统一入口，所有模块的大模型调用都走这里
 */
@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LLMController {

    private final LLMService llmService;

    /**
     * 统一调用入口
     */
    @PostMapping("/call")
    public ApiResponse<LLMResponse> call(@RequestBody LLMRequest request) {
        LLMResponse response = llmService.call(request);
        return ApiResponse.success(response);
    }

    /**
     * 内容审核
     */
    @PostMapping("/review")
    public ApiResponse<LLMResponse.ReviewResult> reviewContent(
            @RequestParam String content,
            @RequestParam(defaultValue = "article") String type) {
        LLMResponse.ReviewResult result = llmService.reviewContent(content, type);
        return ApiResponse.success(result);
    }

    /**
     * 提取标签
     */
    @PostMapping("/tags")
    public ApiResponse<List<String>> extractTags(
            @RequestParam(required = false) String title,
            @RequestParam String content) {
        List<String> tags = llmService.extractTags(title, content);
        return ApiResponse.success(tags);
    }

    /**
     * 分类建议
     */
    @PostMapping("/classify")
    public ApiResponse<String> suggestCategory(
            @RequestParam(required = false) String title,
            @RequestParam String content) {
        String category = llmService.suggestCategory(title, content);
        return ApiResponse.success(category);
    }

    /**
     * 生成摘要
     */
    @PostMapping("/summarize")
    public ApiResponse<String> generateSummary(
            @RequestParam String content,
            @RequestParam(defaultValue = "200") int maxLength) {
        String summary = llmService.generateSummary(content, maxLength);
        return ApiResponse.success(summary);
    }
}