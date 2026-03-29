package com.agenthub.gateway.llm.controller;

import com.agenthub.common.result.ApiResponse;
import com.agenthub.gateway.llm.dto.LLMRequest;
import com.agenthub.gateway.llm.dto.LLMResponse;
import com.agenthub.gateway.llm.service.LLMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 大模型调用控制器
 *
 * 统一入口，所有模块的大模型调用都走这里
 */
@Tag(name = "LLM", description = "大模型调用 API")
@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LLMController {

    private final LLMService llmService;

    /**
     * 统一调用入口
     */
    @Operation(summary = "统一调用入口", description = "调用大模型API")
    @PostMapping("/call")
    public ApiResponse<LLMResponse> call(@RequestBody LLMRequest request) {
        LLMResponse response = llmService.call(request);
        return ApiResponse.success(response);
    }

    /**
     * 内容审核
     */
    @Operation(summary = "内容审核", description = "使用大模型审核内容")
    @PostMapping("/review")
    public ApiResponse<LLMResponse.ReviewResult> reviewContent(
            @Parameter(description = "待审核内容") @RequestParam String content,
            @Parameter(description = "内容类型: article/comment") @RequestParam(defaultValue = "article") String type) {
        LLMResponse.ReviewResult result = llmService.reviewContent(content, type);
        return ApiResponse.success(result);
    }

    /**
     * 提取标签
     */
    @Operation(summary = "提取标签", description = "从内容中提取标签")
    @PostMapping("/tags")
    public ApiResponse<List<String>> extractTags(
            @Parameter(description = "标题") @RequestParam(required = false) String title,
            @Parameter(description = "内容") @RequestParam String content) {
        List<String> tags = llmService.extractTags(title, content);
        return ApiResponse.success(tags);
    }

    /**
     * 分类建议
     */
    @Operation(summary = "分类建议", description = "建议内容分类")
    @PostMapping("/classify")
    public ApiResponse<String> suggestCategory(
            @Parameter(description = "标题") @RequestParam(required = false) String title,
            @Parameter(description = "内容") @RequestParam String content) {
        String category = llmService.suggestCategory(title, content);
        return ApiResponse.success(category);
    }

    /**
     * 生成摘要
     */
    @Operation(summary = "生成摘要", description = "生成内容摘要")
    @PostMapping("/summarize")
    public ApiResponse<String> generateSummary(
            @Parameter(description = "内容") @RequestParam String content,
            @Parameter(description = "最大长度") @RequestParam(defaultValue = "200") int maxLength) {
        String summary = llmService.generateSummary(content, maxLength);
        return ApiResponse.success(summary);
    }
}