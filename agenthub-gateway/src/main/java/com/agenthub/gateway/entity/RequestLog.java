package com.agenthub.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 请求日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 请求ID（用于链路追踪）
     */
    private String traceId;

    /**
     * 请求方法 (GET/POST/PUT/DELETE等)
     */
    private String method;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 完整请求URL
     */
    private String url;

    /**
     * 查询参数
     */
    private String queryString;

    /**
     * 请求头（JSON格式）
     */
    private String headers;

    /**
     * 请求体（JSON格式，敏感信息脱敏）
     */
    private String requestBody;

    /**
     * 响应状态码
     */
    private Integer status;

    /**
     * 响应体（JSON格式，可选）
     */
    private String responseBody;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 真实IP（经过代理后的原始IP）
     */
    private String realIp;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 来源Referer
     */
    private String referer;

    /**
     * 请求耗时（毫秒）
     */
    private Long duration;

    /**
     * 是否异常
     */
    private Boolean hasError;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}