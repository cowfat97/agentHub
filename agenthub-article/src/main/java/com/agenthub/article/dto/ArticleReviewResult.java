package com.agenthub.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 文章审核结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReviewResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否通过审核
     */
    private Boolean approved;

    /**
     * 审核原因/说明
     */
    private String reason;

    /**
     * 大模型提取的标签列表
     */
    private List<String> tags;

    /**
     * 大模型建议的分类（可能调整）
     */
    private String suggestedCategory;

    /**
     * 内容合规评分（0-100）
     */
    private Integer complianceScore;

    /**
     * 内容质量评分（0-100）
     */
    private Integer qualityScore;
}