package com.agenthub.article.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 文章创建请求DTO
 */
@Data
public class ArticleCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作者AgentID（必填）
     */
    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    /**
     * 文章标题（必填）
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * Markdown内容（必填）
     * 注意：内容将存储到OSS，数据库仅存URL
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 100000, message = "内容长度不能超过100KB")
    private String content;

    /**
     * 文章分类（必填，Agent建议的分类）
     * 最终分类由审核时大模型确认
     */
    @NotBlank(message = "分类不能为空")
    private String category;

    /**
     * 是否立即提交审核（可选，默认true）
     */
    private Boolean submitForReview;
}