package com.old.silence.json;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.old.silence.dto.TreeDto;
import com.old.silence.json.data.PageJacksonModule;
import com.old.silence.json.data.TreeNodeJacksonModule;

@AutoConfiguration(before = JacksonAutoConfiguration.class)
public class CustomJacksonAutoConfiguration {

    public static final String DEFAULT_JACKSON_OBJECT_MAPPER_BUILDER_CUSTOMIZER_BEAN_NAME = "defaultJacksonObjectMapperBuilderCustomizer";

    @Bean
    @ConditionalOnMissingBean(name = DEFAULT_JACKSON_OBJECT_MAPPER_BUILDER_CUSTOMIZER_BEAN_NAME)
    Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(){
        return JacksonUtils::customizeWithDefaultBuilder;
    }

    @Configuration(proxyBeanMethods = false)
    static class JacksonMapperConfiguration{

        @Bean
        JacksonMapper jacksonMapper(ObjectMapper objectMapper){
            return new JacksonMapper(objectMapper);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(value = "platform.jackson.data.enabled", havingValue = "true", matchIfMissing = true)
    static class DataJacksonConfiguration{

        @Bean
        @ConditionalOnClass({ Module.class, Page.class, OrderItem.class})
        PageJacksonModule pageJacksonModule(){
            return new PageJacksonModule();
        }

        @Bean
        @ConditionalOnClass({ Module.class, TreeDto.class})
        TreeNodeJacksonModule treeNodeJacksonModule(){
            return new TreeNodeJacksonModule();
        }
    }
}
