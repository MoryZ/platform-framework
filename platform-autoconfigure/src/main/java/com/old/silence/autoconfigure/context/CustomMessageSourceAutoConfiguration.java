package com.old.silence.autoconfigure.context;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import com.old.silence.core.context.MessageSourceAccessorHolder;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({MessageSourceAccessor.class, MessageSourceAccessorHolder.class})
@AutoConfigureAfter(MessageSourceAutoConfiguration.class)
public class CustomMessageSourceAutoConfiguration {

    private CustomMessageSourceAutoConfiguration(){

    }

    @Bean
    @ConditionalOnMissingBean
    static MessageSourceAccessor messageSourceAccessor(MessageSource messageSource){
        return new MessageSourceAccessor(messageSource);
    }

    @Configuration
    static class MessageSourceAccessorHolderConfiguration implements InitializingBean {
        private final MessageSourceAccessor messageSourceAccessor;

        public MessageSourceAccessorHolderConfiguration(MessageSourceAccessor messageSourceAccessor) {
            this.messageSourceAccessor = messageSourceAccessor;
        }

        @Override
        public void afterPropertiesSet() {
            MessageSourceAccessorHolder.setAccessor(messageSourceAccessor);
        }
    }

}
