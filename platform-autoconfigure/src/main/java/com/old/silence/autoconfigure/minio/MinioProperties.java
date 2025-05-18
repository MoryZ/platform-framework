package com.old.silence.autoconfigure.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author MurrayZhang
 */
@ConfigurationProperties(MinioProperties.PREFIX)
public class MinioProperties {

    public static final String PREFIX = "platform.minio";

    private String host;

    private String accessKey;

    private String secretKey;

    private String bucket;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
