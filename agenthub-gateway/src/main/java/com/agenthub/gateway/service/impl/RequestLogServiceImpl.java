package com.agenthub.gateway.service.impl;

import com.agenthub.gateway.entity.RequestLog;
import com.agenthub.gateway.mapper.RequestLogMapper;
import com.agenthub.gateway.service.RequestLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestLogServiceImpl implements RequestLogService {

    private final RequestLogMapper requestLogMapper;

    // ID生成器（生产环境可用雪花算法或分布式ID）
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    @Async
    public void saveAsync(RequestLog requestLog) {
        try {
            save(requestLog);
        } catch (Exception e) {
            log.error("异步保存请求日志失败: traceId={}", requestLog.getTraceId(), e);
        }
    }

    @Override
    public void save(RequestLog requestLog) {
        if (requestLog.getId() == null) {
            requestLog.setId(idGenerator.getAndIncrement());
        }
        requestLogMapper.insert(requestLog);
        log.debug("保存请求日志: id={}, traceId={}", requestLog.getId(), requestLog.getTraceId());
    }

    @Override
    public RequestLog getById(Long id) {
        return requestLogMapper.selectById(id);
    }

    @Override
    public RequestLog getByTraceId(String traceId) {
        return requestLogMapper.selectByTraceId(traceId);
    }

    @Override
    public List<RequestLog> listByPage(int pageNum, int pageSize, String method,
                                        String path, String clientIp, Boolean hasError) {
        int offset = (pageNum - 1) * pageSize;
        return requestLogMapper.selectByPage(offset, pageSize, method, path, clientIp, hasError);
    }

    @Override
    public long count(String method, String path, String clientIp, Boolean hasError) {
        return requestLogMapper.count(method, path, clientIp, hasError);
    }

    @Override
    public int cleanBeforeDays(int days) {
        return requestLogMapper.deleteBeforeDays(days);
    }
}