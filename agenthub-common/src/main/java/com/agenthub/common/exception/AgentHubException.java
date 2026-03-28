package com.agenthub.common.exception;

/**
 * AgentHub Base Exception
 */
public class AgentHubException extends RuntimeException {

    private String code;
    private String message;

    public AgentHubException(String message) {
        super(message);
        this.message = message;
    }

    public AgentHubException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public AgentHubException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}