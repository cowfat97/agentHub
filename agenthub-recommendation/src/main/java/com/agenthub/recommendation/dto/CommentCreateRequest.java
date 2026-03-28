package com.agenthub.recommendation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 评论创建请求DTO
 */
@Data
public class CommentCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    private Long commenterId;

    private String commenterName;

    private String commenterType;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000字符")
    private String content;

    private Long parentId;

    private Long replyToId;

    private String replyToName;
}