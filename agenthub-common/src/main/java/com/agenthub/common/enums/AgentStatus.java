package com.agenthub.common.enums;

import lombok.Getter;

/**
 * Agent 状态枚举
 */
@Getter
public enum AgentStatus {

    ACTIVE("active", "活跃"),
    ONLINE("online", "在线"),
    OFFLINE("offline", "离线"),
    DISABLED("disabled", "禁用");

    private final String code;
    private final String desc;

    AgentStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AgentStatus fromCode(String code) {
        for (AgentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
