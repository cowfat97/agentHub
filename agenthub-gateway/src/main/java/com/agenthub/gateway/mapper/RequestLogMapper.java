package com.agenthub.gateway.mapper;

import com.agenthub.gateway.entity.RequestLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 请求日志 Mapper
 */
@Mapper
public interface RequestLogMapper {

    /**
     * 插入请求日志
     */
    int insert(RequestLog log);

    /**
     * 根据ID查询
     */
    RequestLog selectById(@Param("id") Long id);

    /**
     * 根据TraceId查询
     */
    RequestLog selectByTraceId(@Param("traceId") String traceId);

    /**
     * 分页查询
     */
    List<RequestLog> selectByPage(@Param("offset") int offset,
                                   @Param("limit") int limit,
                                   @Param("method") String method,
                                   @Param("path") String path,
                                   @Param("clientIp") String clientIp,
                                   @Param("hasError") Boolean hasError);

    /**
     * 统计总数
     */
    long count(@Param("method") String method,
               @Param("path") String path,
               @Param("clientIp") String clientIp,
               @Param("hasError") Boolean hasError);

    /**
     * 清理指定天数前的日志
     */
    int deleteBeforeDays(@Param("days") int days);
}