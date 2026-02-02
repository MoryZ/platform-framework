package com.old.silence.web.autoconfigure;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import com.old.silence.web.client.LoggingRequestInterceptor;

@AutoConfiguration(
        after = {HttpClient5AutoConfiguration.class}
)
public class WebClientAutoConfiguration {

    public WebClientAutoConfiguration() {
    }

    @Bean
    @ConditionalOnBean({ClientHttpRequestFactory.class})
    RestTemplateCustomizer restTemplateRequestFactoryCustomizer(ClientHttpRequestFactory requestFactory) {
        return (restTemplate) -> {
            restTemplate.setRequestFactory(requestFactory);
        };
    }

    @Bean
    RestTemplateCustomizer restTemplateLoggingRequestCustomizer() {
        return (restTemplate) -> {
            restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
        };
    }

    @Configuration(
            proxyBeanMethods = false
    )
    @ConditionalOnClass({HttpClient.class})
    @ConditionalOnBean({HttpClient.class})
    @ConditionalOnMissingBean({ClientHttpRequestFactory.class})
    static class HttpClientRequestFactoryConfiguration {
        HttpClientRequestFactoryConfiguration() {
        }

        @Bean
        ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
            return new HttpComponentsClientHttpRequestFactory(httpClient);
        }
    }
}
