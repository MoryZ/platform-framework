package com.old.silence.autoconfigure.minio;

/**
 * @author MurrayZhang
 */
public class OssException extends RuntimeException {

    public OssException(String message) {
        super(message);
    }

    public OssException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
