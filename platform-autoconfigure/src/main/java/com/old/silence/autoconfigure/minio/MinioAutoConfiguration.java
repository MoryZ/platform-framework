package com.old.silence.autoconfigure.minio;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author MurrayZhang
 */
@AutoConfiguration
@ConditionalOnClass(MinioService.class)
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnMissingBean(MinioTemplate.class)
@ConditionalOnProperty(prefix = MinioProperties.PREFIX,
        name = {"host", "bucket", "accessKey", "secretKey"})
public class MinioAutoConfiguration {


    @Bean
    MinioProperties minioProperties() {
        return new MinioProperties();
    }

    @Bean
    MinioService minioService(MinioProperties minioProperties) {
        return new MinioService(minioProperties);
    }

    @Bean
    MinioTemplate minioTemplate(MinioProperties minioProperties, MinioService minioService) {
        return new MinioTemplate(minioProperties.getBucket(), minioService);
    }
}
