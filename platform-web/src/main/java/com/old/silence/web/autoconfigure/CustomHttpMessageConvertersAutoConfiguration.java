package com.old.silence.web.autoconfigure;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import com.old.silence.web.autoconfigure.CustomHttpMessageConvertersAutoConfiguration.NotReactiveWebApplicationCondition;

@ConditionalOnClass({HttpMessageConverter.class})
@Conditional({NotReactiveWebApplicationCondition.class})
@AutoConfiguration(
        before = {HttpMessageConvertersAutoConfiguration.class},
        after = {GsonAutoConfiguration.class, JacksonAutoConfiguration.class, JsonbAutoConfiguration.class}
)
public class CustomHttpMessageConvertersAutoConfiguration {

    @Bean
    HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new CustomHttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({StringHttpMessageConverter.class})
    @ConditionalOnMissingBean({StringHttpMessageConverter.class})
    protected static class StringHttpMessageConverterConfiguration {
        protected StringHttpMessageConverterConfiguration() {
        }

        @Bean
        StringHttpMessageConverter stringHttpMessageConverter(Environment environment) {
            Encoding encoding = Binder.get(environment).bindOrCreate("server.servlet.encoding", Encoding.class);
            StringHttpMessageConverter converter = new StringHttpMessageConverter(encoding.getCharset());
            converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML));
            converter.setWriteAcceptCharset(false);
            return converter;
        }
    }

    static class NotReactiveWebApplicationCondition extends NoneNestedConditions {
        NotReactiveWebApplicationCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(
                type = ConditionalOnWebApplication.Type.REACTIVE
        )
        static class ReactiveWebApplication {
        }
    }
}
