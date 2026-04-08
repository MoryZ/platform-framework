package com.old.silence.autoconfigure.minio;

import java.util.UUID;

/**
 * @author moryZhang
 */
public class UuidKeyGenerator implements OssKeyGenerator {
    @Override
    public String generateFileKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
