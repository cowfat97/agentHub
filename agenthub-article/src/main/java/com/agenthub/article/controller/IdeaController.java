package com.agenthub.article.controller;

import com.agenthub.article.dto.IdeaCreateRequest;
import com.agenthub.article.dto.IdeaListResponse;
import com.agenthub.article.dto.IdeaQueryRequest;
import com.agenthub.article.dto.IdeaUpdateRequest;
import com.agenthub.article.service.IdeaService;
import com.agenthub.common.dto.IdeaDTO;
import com.agenthub.common.result.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 想法控制器
 *
 * 简短内容分享，类似微博
 */
@Tag(name = "Idea", description = "想法 API")
@RestController
@RequestMapping("/api/v1/ideas")
@RequiredArgsConstructor
@Validated
public class IdeaController {

    private final IdeaService ideaService;

    /**
     * 发布想法
     */
    @Operation(summary = "发布想法", description = "发布简短想法，限500字")
    @PostMapping
    public ApiResponse<IdeaDTO> create(@Valid @RequestBody IdeaCreateRequest request) {
        IdeaDTO idea = ideaService.create(request);
        return ApiResponse.success(idea);
    }

    /**
     * 更新想法
     */
    @Operation(summary = "更新想法")
    @PutMapping("/{id}")
    public ApiResponse<IdeaDTO> update(@Parameter(description = "想法ID") @PathVariable Long id,
                                        @RequestBody IdeaUpdateRequest request) {
        request.setId(id);
        IdeaDTO idea = ideaService.update(request);
        return ApiResponse.success(idea);
    }

    /**
     * 删除想法
     */
    @Operation(summary = "删除想法")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "想法ID") @PathVariable Long id,
                                     @Parameter(description = "作者ID") @RequestParam Long authorId) {
        ideaService.delete(id, authorId);
        return ApiResponse.success();
    }

    /**
     * 获取想法详情
     */
    @Operation(summary = "获取想法详情")
    @GetMapping("/{id}")
    public ApiResponse<IdeaDTO> getById(@Parameter(description = "想法ID") @PathVariable Long id) {
        IdeaDTO idea = ideaService.getById(id);
        return ApiResponse.success(idea);
    }

    /**
     * 想法列表
     */
    @Operation(summary = "想法列表")
    @GetMapping
    public ApiResponse<IdeaListResponse> list(IdeaQueryRequest request) {
        IdeaListResponse response = ideaService.list(request);
        return ApiResponse.success(response);
    }

    /**
     * 作者想法列表
     */
    @Operation(summary = "作者想法列表")
    @GetMapping("/author/{authorId}")
    public ApiResponse<IdeaListResponse> findByAuthor(
            @Parameter(description = "作者ID") @PathVariable Long authorId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        IdeaListResponse response = ideaService.findByAuthor(authorId, pageNum, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 点赞想法
     */
    @Operation(summary = "点赞想法")
    @PostMapping("/{id}/like")
    public ApiResponse<IdeaDTO> like(@Parameter(description = "想法ID") @PathVariable Long id) {
        IdeaDTO idea = ideaService.like(id);
        return ApiResponse.success(idea);
    }
}