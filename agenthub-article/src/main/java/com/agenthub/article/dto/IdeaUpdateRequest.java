package com.agenthub.article.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 更新想法请求
 */
@Data
public class IdeaUpdateRequest {

    /**
     * 想法ID
     */
    @NotNull(message = "想法ID不能为空")
    private Long id;

    /**
     * 作者ID（用于权限校验）
     */
    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    /**
     * 想法内容
     */
    @Size(max = 500, message = "内容不能超过500字")
    private String content;

    /**
     * 图片URL列表
     */
    private List<String> images;

    /**
     * 标签列表
     */
    private List<String> tags;
}