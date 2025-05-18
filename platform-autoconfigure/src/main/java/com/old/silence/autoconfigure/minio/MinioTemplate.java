package com.old.silence.autoconfigure.minio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author MurrayZhang
 */
public class MinioTemplate {

    private static final int NO_SPECIFIC_FILE = 612;

    private final MinioService minioService;
    private final String bucket;
    private final OssKeyGenerator ossKeyGenerator;


    public MinioTemplate(String bucket, MinioService minioService) {
        this(minioService, bucket, null);
    }

    public MinioTemplate(MinioService minioService, String bucket, OssKeyGenerator ossKeyGenerator) {
        this.minioService = minioService;
        this.bucket = bucket;
        this.ossKeyGenerator = ossKeyGenerator == null ? new UuidKeyGenerator() : ossKeyGenerator;
    }

    public boolean exists(String fileKey) {
        return minioService.isObjectExist(bucket, fileKey);
    }

    public String upload(String filename, InputStream inputStream) {
        String key = ossKeyGenerator.generateFileKey();
        upload(key, filename, inputStream);
        return key;
    }

    public String upload(String filename, String content) {
        return upload(filename, content.getBytes(StandardCharsets.UTF_8));
    }

    public void upload(String fileKey, String filename, String content) {
        upload(fileKey, filename, content.getBytes(StandardCharsets.UTF_8));
    }

    public String upload(String filename, byte[] bytes) {
        String key = ossKeyGenerator.generateFileKey();
        upload(key, filename, bytes);
        return key;
    }

    public void upload(String fileKey, String filename, byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return;
        }
        upload(fileKey, filename, new ByteArrayInputStream(bytes), bytes.length);
    }

    public void upload(String fileKey, String filename, InputStream inputStream) {
        upload(fileKey, filename, inputStream, -1);
    }

    public void upload(String fileKey, String filename, InputStream inputStream, int length) {
        OssUploadResponse response;
        try {
             response = new OssUploadResponse(fileKey, filename, true);
            minioService.upload(bucket, fileKey + "-" + filename, inputStream, length);
        } catch (Exception e) {
            response = new OssUploadResponse(filename, false, NO_SPECIFIC_FILE ,e.getMessage());
        }

        handleOssErrResponse(response, "UPLOAD", fileKey);

    }

    public void upload(OssUploadRequest request) {
        Objects.requireNonNull(request, "OssUploadRequest is requires");
        Objects.requireNonNull(request.getContent(), "OssUploadRequest content is requires");

        String fileKey = request.getFileKey();
        if (StringUtils.isBlank(fileKey)) {
            fileKey = ossKeyGenerator.generateFileKey();
        }

        Object content = request.getContent();
        if (content instanceof String) {
            upload(fileKey, request.getFilename(), (String) content);
        } else if (content instanceof byte[]) {
            upload(fileKey, request.getFilename(), (byte[]) content);
        } else if (content instanceof InputStream) {
            upload(fileKey, request.getFilename(), (InputStream) content);
        } else {
            throw new IllegalArgumentException("Invalid OssUploadRequest content type [" + content.getClass() + "]");
        }


    }

    public Collection<OssUploadResponse> bulkUpload(Collection<OssUploadRequest> requests) {
        return requests.stream().map(request -> {

                    try {
                        upload(request);
                        return new OssUploadResponse(request.getFileKey(), request.getFilename(), true);
                    } catch (Exception e) {
                        return new OssUploadResponse(request.getFilename(), false);
                    }
                }
        ).collect(Collectors.toList());
    }

    public InputStream download(String fileKey, String filename) {
        var inputStream = minioService.getObject(bucket, fileKey + "-" + filename);
        OssUploadResponse response = new OssUploadResponse(filename, true);
        handleOssErrResponse(response, "DOWNLOAD", fileKey);
        return inputStream;
    }

    public String getInternetUrl(String fileKey, String filename) {
        return minioService.getPresignedObjectUrl(bucket, fileKey, filename);

    }

    private void handleOssErrResponse(OssUploadResponse response, String operationName, String fileKey) {
        if (response == null) {
            throw new OssException("Oss operation [" + operationName + "] failed.");
        }

        if (!response.isSuccessful()) {
            String template = "Oss operation [$s] failed with bucket [%s], fileKey [%s], error code [%d], error message [%s]";
            String message = String.format(template, operationName, bucket, fileKey, response.getCode(), response.getError());
            throw new OssException(message);
        }
    }


}
