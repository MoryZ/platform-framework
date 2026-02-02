package com.old.silence.webmvc.exception;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.core.exception.PlatformException;

public abstract class PlatformExceptionHandlerDelegatedExceptionHandler implements MvcExceptionHandler {

    private final PlatformExceptionHandler delegate;

    private final Class<? extends Throwable> exceptionType;

    protected PlatformExceptionHandlerDelegatedExceptionHandler(MessageSourceAccessor messageSourceAccessor,
                                                                Class<? extends Throwable> exceptionType) {

        this(messageSourceAccessor, exceptionType, true);
    }

    protected PlatformExceptionHandlerDelegatedExceptionHandler(MessageSourceAccessor messageSourceAccessor,
                                                                Class<? extends Throwable> exceptionType, boolean warningExceptionLoggingRequired) {

        this.delegate = new PlatformExceptionHandler(messageSourceAccessor, warningExceptionLoggingRequired);
        this.exceptionType = exceptionType;
    }
    @Override
    public Class<? extends Throwable> getSupportedExceptionType() {
        return exceptionType;
    }

    @Override
    public ResponseEntity<?> handle(Throwable exception, WebRequest request, String systemIdentifier) {
        return delegate.handle(createPlatformException(exception, request), request, systemIdentifier);
    }

    protected abstract PlatformException createPlatformException(Throwable exception, WebRequest request);
}

