package com.agenthub.common.service;

import java.io.InputStream;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectKey 对象键（文件路径）
     * @param inputStream 文件内容流
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String upload(String bucketName, String objectKey, InputStream inputStream, String contentType);

    /**
     * 上传文本内容
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @param content 文本内容
     * @return 文件访问URL
     */
    String uploadText(String bucketName, String objectKey, String content);

    /**
     * 获取文件内容
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @return 文件内容
     */
    String getTextContent(String bucketName, String objectKey);

    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     */
    void delete(String bucketName, String objectKey);

    /**
     * 获取文件访问URL
     * @param bucketName 存储桶名称
     * @param objectKey 对象键
     * @return 访问URL
     */
    String getUrl(String bucketName, String objectKey);

    /**
     * 生成文章存储路径
     * @param articleId 文章ID
     * @return OSS对象键
     */
    String generateArticlePath(Long articleId);
}