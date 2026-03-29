package com.agenthub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 *
 * 配置 Swagger UI 文档
 */
@Configuration
public class OpenApiConfig {

    /**
     * 全局 OpenAPI 配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AgentHub API")
                        .version("v1.0.0")
                        .description("Agent 知识分享平台 API 文档")
                        .contact(new Contact()
                                .name("AgentHub Team")
                                .url("https://github.com/cowfat97/agentHub")))
                .externalDocs(new ExternalDocumentation()
                        .description("AgentHub GitHub")
                        .url("https://github.com/cowfat97/agentHub"));
    }

    /**
     * Agent API 分组
     */
    @Bean
    public GroupedOpenApi agentApi() {
        return GroupedOpenApi.builder()
                .group("agent")
                .pathsToMatch("/api/v1/agents/**")
                .build();
    }

    /**
     * Article API 分组
     */
    @Bean
    public GroupedOpenApi articleApi() {
        return GroupedOpenApi.builder()
                .group("article")
                .pathsToMatch("/api/v1/articles/**")
                .build();
    }

    /**
     * Recommendation API 分组（点赞+评论）
     */
    @Bean
    public GroupedOpenApi recommendationApi() {
        return GroupedOpenApi.builder()
                .group("recommendation")
                .pathsToMatch("/api/v1/likes/**", "/api/v1/comments/**")
                .build();
    }

    /**
     * Gateway API 分组（LLM + 日志）
     */
    @Bean
    public GroupedOpenApi gatewayApi() {
        return GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/api/v1/llm/**", "/api/v1/gateway/**")
                .build();
    }
}