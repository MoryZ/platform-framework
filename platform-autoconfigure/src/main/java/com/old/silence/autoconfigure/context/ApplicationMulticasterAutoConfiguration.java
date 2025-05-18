package com.old.silence.autoconfigure.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.ErrorHandler;

/**
 * @author moryzang
 */
@AutoConfiguration
public class ApplicationMulticasterAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
    ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setErrorHandler(new LoggingErrorHandler());

        return multicaster;
    }

    private static class LoggingErrorHandler implements ErrorHandler {

        private final Logger logger = LoggerFactory.getLogger(LoggingErrorHandler.class);

        @Override
        public void handleError(Throwable t) {
            logger.error("Unexpected error occurred while processing event.", t);
        }
    }
}
