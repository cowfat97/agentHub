package com.agenthub.gateway.controller;

import com.agenthub.common.result.ApiResponse;
import com.agenthub.gateway.dto.RequestLogListResponse;
import com.agenthub.gateway.entity.RequestLog;
import com.agenthub.gateway.service.RequestLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 请求日志控制器
 *
 * 日志查询接口仅管理员可访问
 */
@Tag(name = "RequestLog", description = "请求日志 API（管理员权限）")
@RestController
@RequestMapping("/api/v1/gateway/logs")
@RequiredArgsConstructor
public class RequestLogController {

    private final RequestLogService requestLogService;

    /**
     * 分页查询请求日志
     * 仅管理员可访问
     */
    @Operation(summary = "分页查询请求日志", description = "管理员查询请求日志")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RequestLogListResponse> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize,
            @Parameter(description = "请求方法") @RequestParam(required = false) String method,
            @Parameter(description = "请求路径") @RequestParam(required = false) String path,
            @Parameter(description = "客户端IP") @RequestParam(required = false) String clientIp,
            @Parameter(description = "是否有错误") @RequestParam(required = false) Boolean hasError) {

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
    @Operation(summary = "根据ID查询请求日志")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RequestLog> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        RequestLog log = requestLogService.getById(id);
        return ApiResponse.success(log);
    }

    /**
     * 根据TraceId查询请求日志
     * 仅管理员可访问
     */
    @Operation(summary = "根据TraceId查询请求日志")
    @GetMapping("/trace/{traceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RequestLog> getByTraceId(@Parameter(description = "TraceId") @PathVariable String traceId) {
        RequestLog log = requestLogService.getByTraceId(traceId);
        return ApiResponse.success(log);
    }

    /**
     * 清理历史日志
     * 仅管理员可访问
     */
    @Operation(summary = "清理历史日志", description = "清理指定天数前的日志")
    @DeleteMapping("/clean")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> clean(@Parameter(description = "保留天数") @RequestParam(defaultValue = "30") int days) {
        int deleted = requestLogService.cleanBeforeDays(days);
        return ApiResponse.success(deleted);
    }
}