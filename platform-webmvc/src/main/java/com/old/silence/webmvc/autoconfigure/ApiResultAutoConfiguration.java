package com.old.silence.webmvc.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import com.old.silence.webmvc.handler.ApiResultHandler;

/**
 * @author moryzang
 */
@Configuration
@ConditionalOnWebApplication // 仅在Web应用中生效
@ConditionalOnClass(RestController.class) // 确保RestController存在
public class ApiResultAutoConfiguration {

    @Bean
    public ApiResultHandler apiResultHandler() {
        return new ApiResultHandler();
    }
}