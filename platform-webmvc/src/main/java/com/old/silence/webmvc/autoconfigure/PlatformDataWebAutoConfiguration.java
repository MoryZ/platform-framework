package com.old.silence.webmvc.autoconfigure;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.old.silence.core.security.UserContextAware;
import com.old.silence.web.data.SortHandlerMethodArgumentResolver;
import com.old.silence.webmvc.data.UserHeaderAuditorAware;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfiguration(after = RepositoryRestMvcAutoConfiguration.class)
public class PlatformDataWebAutoConfiguration implements WebMvcConfigurer {

    private final String defaultAuditor;

    public PlatformDataWebAutoConfiguration(@Value("${platform.data.web.default-auditor:SYSTEM}") String defaultAuditor) {
        this.defaultAuditor = defaultAuditor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SortHandlerMethodArgumentResolver());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            name = {"platform.data.web.user.header-auditor-enabled"},
            matchIfMissing = true
    )
    UserContextAware<String> userContextAware() {
        return new UserHeaderAuditorAware(this.defaultAuditor);
    }

}
