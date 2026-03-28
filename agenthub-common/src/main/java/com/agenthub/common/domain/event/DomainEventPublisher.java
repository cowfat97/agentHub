package com.agenthub.common.domain.event;

import java.util.List;

/**
 * 领域事件发布器接口
 */
public interface DomainEventPublisher {

    /**
     * 发布单个事件
     */
    void publish(DomainEvent event);

    /**
     * 批量发布事件
     */
    void publishAll(List<DomainEvent> events);
}
