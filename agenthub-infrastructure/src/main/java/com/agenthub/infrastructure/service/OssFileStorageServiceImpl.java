package com.agenthub.infrastructure.service;

import com.agenthub.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * OSS文件存储服务实现（阿里云OSS）
 * TODO: 需要配置OSS SDK依赖和凭证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssFileStorageServiceImpl implements FileStorageService {

    // TODO: 注入OSS客户端
    // private final OSS ossClient;

    // OSS配置（从配置文件读取）
    private static final String DEFAULT_BUCKET = "agenthub-articles";
    private static final String ARTICLE_PATH_PREFIX = "articles/";

    @Override
    public String upload(String bucketName, String objectKey, InputStream inputStream, String contentType) {
        // TODO: 实现OSS上传
        // ossClient.putObject(bucketName, objectKey, inputStream, metadata);
        log.info("OSS上传文件: bucket={}, key={}, contentType={}", bucketName, objectKey, contentType);
        return getUrl(bucketName, objectKey);
    }

    @Override
    public String uploadText(String bucketName, String objectKey, String content) {
        // TODO: 实现文本内容上传
        log.info("OSS上传文本: bucket={}, key={}, contentLength={}", bucketName, objectKey, content.length());
        // 模拟返回URL
        return String.format("https://%s.oss-cn-hangzhou.aliyuncs.com/%s", bucketName, objectKey);
    }

    @Override
    public String getTextContent(String bucketName, String objectKey) {
        // TODO: 实现OSS内容获取
        log.info("OSS获取文本: bucket={}, key={}", bucketName, objectKey);
        return null;
    }

    @Override
    public void delete(String bucketName, String objectKey) {
        // TODO: 实现OSS删除
        log.info("OSS删除文件: bucket={}, key={}", bucketName, objectKey);
    }

    @Override
    public String getUrl(String bucketName, String objectKey) {
        // TODO: 根据实际OSS配置生成URL
        return String.format("https://%s.oss-cn-hangzhou.aliyuncs.com/%s", bucketName, objectKey);
    }

    @Override
    public String generateArticlePath(Long articleId) {
        // 生成文章存储路径：articles/{articleId}/content.md
        return String.format("%s%d/content.md", ARTICLE_PATH_PREFIX, articleId);
    }
}