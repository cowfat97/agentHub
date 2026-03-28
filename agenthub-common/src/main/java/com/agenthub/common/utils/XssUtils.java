package com.agenthub.common.utils;

import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * XSS过滤工具类
 *
 * 防止跨站脚本攻击，对用户输入进行HTML转义和危险标签过滤
 */
public class XssUtils {

    /**
     * 危险HTML标签正则（script, iframe, object等）
     */
    private static final Pattern DANGEROUS_TAGS = Pattern.compile(
            "<(script|iframe|object|embed|applet|meta|link|style|form|input|button|textarea|select|option)[^>]*>",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 危险事件属性正则（onclick, onload等）
     */
    private static final Pattern DANGEROUS_EVENTS = Pattern.compile(
            "\\s+(on[a-z]+)\\s*=\\s*['\"]?[^'\">\\s]*['\"]?",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * JavaScript代码正则
     */
    private static final Pattern JAVASCRIPT_CODE = Pattern.compile(
            "(javascript|vbscript|expression|data):",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * HTML实体编码转义
     *
     * 对所有HTML特殊字符进行转义，防止XSS攻击
     * 适用于纯文本内容的转义
     *
     * @param content 原始内容
     * @return 转义后的内容
     */
    public static String escape(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        return HtmlUtils.htmlEscape(content);
    }

    /**
     * 富文本内容过滤
     *
     * 移除危险HTML标签和事件属性，保留安全的格式标签
     * 适用于Markdown等富文本内容
     *
     * @param content 原始内容
     * @return 过滤后的内容
     */
    public static String sanitizeRichText(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String result = content;

        // 移除危险标签
        result = DANGEROUS_TAGS.matcher(result).replaceAll("");

        // 移除危险事件属性
        result = DANGEROUS_EVENTS.matcher(result).replaceAll("");

        // 移除javascript代码
        result = JAVASCRIPT_CODE.matcher(result).replaceAll("");

        return result;
    }

    /**
     * Markdown内容过滤
     *
     * Markdown本身是纯文本格式，但可能包含HTML片段
     * 仅过滤危险部分，保留Markdown语法
     *
     * @param content Markdown内容
     * @return 过滤后的内容
     */
    public static String sanitizeMarkdown(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String result = content;

        // Markdown中的HTML代码块需要过滤
        result = DANGEROUS_TAGS.matcher(result).replaceAll("");
        result = DANGEROUS_EVENTS.matcher(result).replaceAll("");
        result = JAVASCRIPT_CODE.matcher(result).replaceAll("");

        return result;
    }

    /**
     * 评论内容过滤
     *
     * 评论内容需要严格过滤，防止XSS攻击
     *
     * @param content 评论内容
     * @return 过滤后的内容
     */
    public static String sanitizeComment(String content) {
        return escape(content);
    }

    /**
     * 文章内容过滤
     *
     * 文章内容是Markdown格式，允许保留格式语法
     *
     * @param content 文章内容
     * @return 过滤后的内容
     */
    public static String sanitizeArticle(String content) {
        return sanitizeMarkdown(content);
    }

    /**
     * 标题过滤
     *
     * 标题需要严格过滤，不允许任何HTML标签
     *
     * @param title 标题内容
     * @return 过滤后的标题
     */
    public static String sanitizeTitle(String title) {
        return escape(title);
    }

    /**
     * 标签过滤
     *
     * 标签需要严格过滤，只保留安全字符
     *
     * @param tag 标签内容
     * @return 过滤后的标签
     */
    public static String sanitizeTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return tag;
        }
        // 只保留字母、数字、中文、下划线、横线
        return tag.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5_-]", "");
    }

    /**
     * 检查内容是否包含危险代码
     *
     * @param content 内容
     * @return 是否包含危险代码
     */
    public static boolean containsDangerousCode(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        return DANGEROUS_TAGS.matcher(content).find()
                || DANGEROUS_EVENTS.matcher(content).find()
                || JAVASCRIPT_CODE.matcher(content).find();
    }
}