package com.agenthub.gateway.controller;

import com.agenthub.common.result.ApiResponse;
import com.agenthub.gateway.dto.RequestLogListResponse;
import com.agenthub.gateway.entity.RequestLog;
import com.agenthub.gateway.service.RequestLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 请求日志控制器
 *
 * 日志查询接口仅管理员可访问
 */
@RestController
@RequestMapping("/api/v1/gateway/logs")
@RequiredArgsConstructor
public class RequestLogController {

    private final RequestLogService requestLogService;

    /**
     * 分页查询请求日志
     * 仅管理员可访问
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RequestLogListResponse> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String clientIp,
            @RequestParam(required = false) Boolean hasError) {

        long total = requestLogService.count(method, path, clientIp, hasError);
        java.util.List<RequestLog> logs = requestLogService.listByPage(
                pageNum, pageSize, method, path, clientIp, hasError);

        RequestLogListResponse response = RequestLogListResponse.builder()
                .logs(logs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();

        return ApiResponse.success(response);
    }

    /**
     * 根据ID查询请求日志
     * 仅管理员可访问
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RequestLog> getById(@PathVariable Long id) {
        RequestLog log = requestLogService.getById(id);
        return ApiResponse.success(log);
    }

    /**
     * 根据TraceId查询请求日志
     * 仅管理员可访问
     */
    @GetMapping("/trace/{traceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RequestLog> getByTraceId(@PathVariable String traceId) {
        RequestLog log = requestLogService.getByTraceId(traceId);
        return ApiResponse.success(log);
    }

    /**
     * 清理历史日志
     * 仅管理员可访问
     */
    @DeleteMapping("/clean")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> clean(@RequestParam(defaultValue = "30") int days) {
        int deleted = requestLogService.cleanBeforeDays(days);
        return ApiResponse.success(deleted);
    }
}