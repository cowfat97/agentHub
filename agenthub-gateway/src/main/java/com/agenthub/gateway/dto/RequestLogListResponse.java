package com.agenthub.gateway.dto;

import com.agenthub.gateway.entity.RequestLog;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 请求日志查询响应
 */
@Data
@Builder
public class RequestLogListResponse {

    /**
     * 日志列表
     */
    private List<RequestLog> logs;

    /**
     * 总数
     */
    private long total;

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 是否有更多
     */
    private boolean hasMore;
}