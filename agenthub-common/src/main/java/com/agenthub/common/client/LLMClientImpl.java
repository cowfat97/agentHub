package com.agenthub.common.client;

import com.agenthub.common.result.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大模型客户端实现
 *
 * 通过HTTP调用网关的大模型服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LLMClientImpl implements LLMClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${agenthub.gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @Override
    public ApiResponse<ReviewResult> reviewContent(String content, String type) {
        String url = gatewayUrl + "/api/v1/llm/review?content=" + encode(content) + "&type=" + type;
        try {
            @SuppressWarnings("unchecked")
            ApiResponse<Map<String, Object>> response = restTemplate.postForObject(url, null, ApiResponse.class);
            if (response != null && response.getData() != null) {
                ReviewResult result = objectMapper.convertValue(response.getData(), ReviewResult.class);
                ApiResponse<ReviewResult> resultResponse = new ApiResponse<>();
                resultResponse.setCode(response.getCode());
                resultResponse.setMessage(response.getMessage());
                resultResponse.setData(result);
                resultResponse.setTimestamp(response.getTimestamp());
                return resultResponse;
            }
            return ApiResponse.error("审核服务返回空结果");
        } catch (Exception e) {
            log.error("调用大模型审核失败", e);
            return ApiResponse.error("调用大模型服务失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<String>> extractTags(String title, String content) {
        String url = gatewayUrl + "/api/v1/llm/tags?content=" + encode(content);
        if (title != null && !title.isEmpty()) {
            url += "&title=" + encode(title);
        }
        try {
            @SuppressWarnings("unchecked")
            ApiResponse<List<String>> response = restTemplate.postForObject(url, null, ApiResponse.class);
            return response != null ? response : ApiResponse.error("标签提取服务返回空结果");
        } catch (Exception e) {
            log.error("调用大模型标签提取失败", e);
            return ApiResponse.error("调用大模型服务失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> suggestCategory(String title, String content) {
        String url = gatewayUrl + "/api/v1/llm/classify?content=" + encode(content);
        if (title != null && !title.isEmpty()) {
            url += "&title=" + encode(title);
        }
        try {
            @SuppressWarnings("unchecked")
            ApiResponse<String> response = restTemplate.postForObject(url, null, ApiResponse.class);
            return response != null ? response : ApiResponse.error("分类服务返回空结果");
        } catch (Exception e) {
            log.error("调用大模型分类失败", e);
            return ApiResponse.error("调用大模型服务失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> generateSummary(String content, int maxLength) {
        String url = gatewayUrl + "/api/v1/llm/summarize?content=" + encode(content) + "&maxLength=" + maxLength;
        try {
            @SuppressWarnings("unchecked")
            ApiResponse<String> response = restTemplate.postForObject(url, null, ApiResponse.class);
            return response != null ? response : ApiResponse.error("摘要服务返回空结果");
        } catch (Exception e) {
            log.error("调用大模型摘要生成失败", e);
            return ApiResponse.error("调用大模型服务失败: " + e.getMessage());
        }
    }

    private String encode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}