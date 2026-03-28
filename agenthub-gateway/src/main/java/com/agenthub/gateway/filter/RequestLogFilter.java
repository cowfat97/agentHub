package com.agenthub.gateway.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.agenthub.gateway.entity.RequestLog;
import com.agenthub.gateway.service.RequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 请求日志过滤器
 * 记录所有HTTP请求的详细信息
 */
@Slf4j
@Component
@Order(1)
public class RequestLogFilter extends OncePerRequestFilter {

    @Autowired
    private RequestLogService requestLogService;

    /**
     * 不记录日志的路径（前缀匹配）
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/actuator",
            "/favicon.ico",
            "/error",
            "/static",
            "/swagger",
            "/v2/api-docs",
            "/v3/api-docs",
            "/webjars"
    );

    /**
     * 敏感请求头（不记录值）
     */
    private static final Set<String> SENSITIVE_HEADERS = new HashSet<>(Arrays.asList(
            "authorization", "cookie", "token", "password", "secret"
    ));

    /**
     * 敏感请求参数名（脱敏处理）
     */
    private static final Set<String> SENSITIVE_PARAMS = new HashSet<>(Arrays.asList(
            "password", "pwd", "token", "secret", "key", "authorization"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String traceId = IdUtil.fastSimpleUUID();

        // 包装Request和Response以便多次读取
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // 设置TraceId到响应头
        response.setHeader("X-Trace-Id", traceId);

        RequestLog.RequestLogBuilder logBuilder = RequestLog.builder()
                .traceId(traceId)
                .createdAt(LocalDateTime.now());

        boolean hasError = false;
        String errorMessage = null;

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception e) {
            hasError = true;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            try {
                // 构建日志记录
                buildRequestLog(logBuilder, wrappedRequest, wrappedResponse, duration, hasError, errorMessage);
                RequestLog requestLog = logBuilder.build();

                // 异步保存日志
                requestLogService.saveAsync(requestLog);

                // 控制台输出（开发环境）
                if (log.isDebugEnabled()) {
                    log.debug("请求日志: traceId={}, method={}, path={}, ip={}, duration={}ms",
                            traceId, requestLog.getMethod(), requestLog.getPath(),
                            requestLog.getClientIp(), duration);
                }
            } catch (Exception e) {
                log.error("记录请求日志失败", e);
            }

            // 复制响应内容到原始响应
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * 构建请求日志
     */
    private void buildRequestLog(RequestLog.RequestLogBuilder builder,
                                  ContentCachingRequestWrapper request,
                                  ContentCachingResponseWrapper response,
                                  long duration, boolean hasError, String errorMessage) {

        // 基本请求信息
        builder.method(request.getMethod())
                .path(request.getRequestURI())
                .url(request.getRequestURL().toString())
                .queryString(request.getQueryString())
                .duration(duration)
                .status(response.getStatus())
                .hasError(hasError)
                .errorMessage(errorMessage);

        // IP信息
        builder.clientIp(getClientIp(request))
                .realIp(getRealIp(request));

        // User-Agent和Referer
        builder.userAgent(request.getHeader("User-Agent"))
                .referer(request.getHeader("Referer"));

        // 请求头（脱敏处理）
        builder.headers(getHeadersJson(request));

        // 请求体（脱敏处理）
        builder.requestBody(getRequestBody(request));

        // 响应体（可选，大响应不记录）
        builder.responseBody(getResponseBody(response));
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取真实IP（经过代理后的原始IP）
     */
    private String getRealIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotEmpty(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return getClientIp(request);
    }

    /**
     * 获取请求头JSON（脱敏处理）
     */
    private String getHeadersJson(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (SENSITIVE_HEADERS.contains(name.toLowerCase())) {
                headers.put(name, "******");
            } else {
                headers.put(name, request.getHeader(name));
            }
        }
        return JSONUtil.toJsonStr(headers);
    }

    /**
     * 获取请求体（脱敏处理）
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] content = request.getContentAsByteArray();
            if (content.length == 0) {
                return null;
            }

            String body = new String(content, StandardCharsets.UTF_8);

            // 限制大小
            if (body.length() > 4096) {
                body = body.substring(0, 4096) + "...(truncated)";
            }

            // 尝试JSON脱敏
            try {
                if (body.trim().startsWith("{")) {
                    Map<String, Object> map = JSONUtil.toBean(body, Map.class);
                    for (String key : SENSITIVE_PARAMS) {
                        if (map.containsKey(key)) {
                            map.put(key, "******");
                        }
                    }
                    return JSONUtil.toJsonStr(map);
                }
            } catch (Exception ignored) {
                // 非JSON格式，直接返回
            }

            return body;
        } catch (Exception e) {
            log.warn("读取请求体失败", e);
            return null;
        }
    }

    /**
     * 获取响应体
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length == 0) {
                return null;
            }

            // 只记录JSON响应
            String contentType = response.getContentType();
            if (contentType == null || !contentType.contains("application/json")) {
                return null;
            }

            String body = new String(content, StandardCharsets.UTF_8);

            // 限制大小
            if (body.length() > 4096) {
                body = body.substring(0, 4096) + "...(truncated)";
            }

            return body;
        } catch (Exception e) {
            log.warn("读取响应体失败", e);
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String excludePath : EXCLUDE_PATHS) {
            if (path.startsWith(excludePath)) {
                return true;
            }
        }
        return false;
    }
}