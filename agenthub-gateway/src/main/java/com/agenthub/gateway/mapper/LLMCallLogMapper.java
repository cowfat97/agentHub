package com.agenthub.gateway.mapper;

import com.agenthub.gateway.entity.LLMCallLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 大模型调用日志 Mapper
 */
@Mapper
public interface LLMCallLogMapper {

    int insert(LLMCallLog log);

    LLMCallLog selectById(@Param("id") Long id);

    List<LLMCallLog> selectByPage(@Param("offset") int offset,
                                   @Param("limit") int limit,
                                   @Param("requestType") String requestType,
                                   @Param("sourceModule") String sourceModule,
                                   @Param("success") Boolean success);

    long count(@Param("requestType") String requestType,
               @Param("sourceModule") String sourceModule,
               @Param("success") Boolean success);

    long countTokensByDate(@Param("date") String date);

    int deleteBeforeDays(@Param("days") int days);
}