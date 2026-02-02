package com.old.silence.webmvc.exception;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;

public abstract class AbstractMvcExceptionHandler implements MvcExceptionHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final MessageSourceAccessor messageSourceAccessor;

    private final Class<? extends Throwable> exceptionType;

    private final boolean warningExceptionLoggingRequired;

    protected AbstractMvcExceptionHandler(Class<? extends Throwable> exceptionType) {
        this(exceptionType, false);
    }

    protected AbstractMvcExceptionHandler(Class<? extends Throwable> exceptionType, boolean warningExceptionLoggingRequired) {
        this(null, exceptionType, warningExceptionLoggingRequired);
    }

    protected AbstractMvcExceptionHandler(MessageSourceAccessor messageSourceAccessor, Class<? extends Throwable> exceptionType) {
        this(messageSourceAccessor, exceptionType, false);
    }
    protected AbstractMvcExceptionHandler(MessageSourceAccessor messageSourceAccessor, Class<? extends Throwable> exceptionType,
                                          boolean warningExceptionLoggingRequired) {

        this.messageSourceAccessor = messageSourceAccessor;
        this.exceptionType = Objects.requireNonNull(exceptionType, "exceptionType is required");
        this.warningExceptionLoggingRequired = warningExceptionLoggingRequired;
    }

    @Override
    public Class<? extends Throwable> getSupportedExceptionType() {
        return exceptionType;
    }

    protected void logException(HttpStatus status, String message, Exception e) {

        if (status.is5xxServerError()) {
            logger.error(message, e);
            return;
        }

        if (warningExceptionLoggingRequired) {
            logger.warn(StringUtils.defaultString(message), e);
        } else if (StringUtils.isNotEmpty(message)) {
            logger.warn(message);
        }
    }
}
