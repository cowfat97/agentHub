package com.agenthub.common.exception;

import com.agenthub.common.domain.exception.AgentAlreadyExistsException;
import com.agenthub.common.domain.exception.AgentNotFoundException;
import com.agenthub.common.domain.exception.ArticleNotFoundException;
import com.agenthub.common.domain.exception.ArticlePermissionDeniedException;
import com.agenthub.common.domain.exception.ArticleStateException;
import com.agenthub.common.domain.exception.CommentNotFoundException;
import com.agenthub.common.domain.exception.DomainException;
import com.agenthub.common.domain.exception.DuplicateLikeException;
import com.agenthub.common.result.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理领域异常
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleDomainException(DomainException e) {
        log.warn("Domain exception: {}", e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理 Agent 不存在异常
     */
    @ExceptionHandler(AgentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleAgentNotFoundException(AgentNotFoundException e) {
        log.warn("Agent not found: {}", e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理 Agent 已存在异常
     */
    @ExceptionHandler(AgentAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleAgentAlreadyExistsException(AgentAlreadyExistsException e) {
        log.warn("Agent already exists: {}", e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理文章不存在异常
     */
    @ExceptionHandler(ArticleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleArticleNotFoundException(ArticleNotFoundException e) {
        log.warn("Article not found: {}", e.getMessage());
        return ApiResponse.error("ARTICLE_NOT_FOUND", e.getMessage());
    }

    /**
     * 处理文章权限异常
     */
    @ExceptionHandler(ArticlePermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleArticlePermissionDeniedException(ArticlePermissionDeniedException e) {
        log.warn("Article permission denied: {}", e.getMessage());
        return ApiResponse.error("PERMISSION_DENIED", e.getMessage());
    }

    /**
     * 处理文章状态异常
     */
    @ExceptionHandler(ArticleStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleArticleStateException(ArticleStateException e) {
        log.warn("Article state exception: {}", e.getMessage());
        return ApiResponse.error("INVALID_STATE", e.getMessage());
    }

    /**
     * 处理评论不存在异常
     */
    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleCommentNotFoundException(CommentNotFoundException e) {
        log.warn("Comment not found: {}", e.getMessage());
        return ApiResponse.error("COMMENT_NOT_FOUND", e.getMessage());
    }

    /**
     * 处理重复点赞异常
     */
    @ExceptionHandler(DuplicateLikeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDuplicateLikeException(DuplicateLikeException e) {
        log.warn("Duplicate like: {}", e.getMessage());
        return ApiResponse.error("DUPLICATE_LIKE", e.getMessage());
    }

    /**
     * 处理 AgentHub 业务异常
     */
    @ExceptionHandler(AgentHubException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleAgentHubException(AgentHubException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(Exception e) {
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            if (ex.getBindingResult().hasErrors()) {
                message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            }
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            if (ex.getBindingResult().hasErrors()) {
                message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            }
        }
        log.warn("Validation failed: {}", message);
        return ApiResponse.error(400, message);
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ApiResponse.error(500, "系统内部错误");
    }
}
