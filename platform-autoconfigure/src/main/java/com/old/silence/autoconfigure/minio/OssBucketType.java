package com.old.silence.autoconfigure.minio;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author MurrayZhang
 */
public enum OssBucketType {

    PRIVATE {
        @Override
        public String getDownloadUrl(MinioService ossService, String bucket, String fileKey, String filename) {
            return ossService.getPresignedObjectUrl(bucket, fileKey, filename);
        }
    },
    PUBLIC {
        @Override
        public String getDownloadUrl(MinioService ossService, String bucket, String fileKey, String filename) {
            return ossService.getPresignedObjectUrl(bucket, fileKey, filename);
        }
    };

    public String getDownloadUrl(MinioService ossService, String bucket, String fileKey) {
        try {
            return getDownloadUrl(ossService, bucket, fileKey, null);
        } catch (OssException ossException) {
            throw new UndeclaredThrowableException(ossException);
        }
    }

    public abstract String getDownloadUrl(MinioService ossService, String bucket, String fileKey, String filename);
}
