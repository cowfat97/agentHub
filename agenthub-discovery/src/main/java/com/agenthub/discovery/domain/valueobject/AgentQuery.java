package com.agenthub.discovery.domain.valueobject;

import lombok.Getter;

/**
 * Agent 查询条件值对象
 */
@Getter
public class AgentQuery {

    private final String name;
    private final String status;
    private final Integer page;
    private final Integer size;

    private AgentQuery(Builder builder) {
        this.name = builder.name;
        this.status = builder.status;
        this.page = builder.page != null ? builder.page : 1;
        this.size = builder.size != null ? builder.size : 20;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String status;
        private Integer page;
        private Integer size;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder size(Integer size) {
            this.size = size;
            return this;
        }

        public AgentQuery build() {
            return new AgentQuery(this);
        }
    }
}