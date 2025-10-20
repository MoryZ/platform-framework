package com.old.silence.autoconfigure.context;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import com.old.silence.core.context.MessageSourceAccessorHolder;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({MessageSourceAccessor.class, MessageSourceAccessorHolder.class})
@AutoConfigureAfter(MessageSourceAutoConfiguration.class)
public class CustomMessageSourceAutoConfiguration implements InitializingBean{

    private final MessageSource messageSource;

    public CustomMessageSourceAutoConfiguration(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MessageSourceAccessorHolder.setAccessor(new MessageSourceAccessor(this.messageSource));
    }
}
