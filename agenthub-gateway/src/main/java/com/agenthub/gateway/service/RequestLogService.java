package com.agenthub.gateway.service;

import com.agenthub.gateway.entity.RequestLog;

import java.util.List;

/**
 * 请求日志服务接口
 */
public interface RequestLogService {

    /**
     * 异步保存请求日志
     */
    void saveAsync(RequestLog requestLog);

    /**
     * 同步保存请求日志
     */
    void save(RequestLog requestLog);

    /**
     * 根据ID查询
     */
    RequestLog getById(Long id);

    /**
     * 根据TraceId查询
     */
    RequestLog getByTraceId(String traceId);

    /**
     * 分页查询
     */
    List<RequestLog> listByPage(int pageNum, int pageSize, String method,
                                 String path, String clientIp, Boolean hasError);

    /**
     * 统计总数
     */
    long count(String method, String path, String clientIp, Boolean hasError);

    /**
     * 清理指定天数前的日志
     */
    int cleanBeforeDays(int days);
}