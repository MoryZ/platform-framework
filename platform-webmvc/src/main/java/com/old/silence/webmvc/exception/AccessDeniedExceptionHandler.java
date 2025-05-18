package com.old.silence.webmvc.exception;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.core.context.CommonErrors;
import com.old.silence.core.exception.PlatformException;

public class AccessDeniedExceptionHandler extends PlatformExceptionHandlerDelegatedExceptionHandler {

    public AccessDeniedExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor, AccessDeniedException.class, false);
    }

    @Override
    protected PlatformException createPlatformException(Throwable exception, WebRequest request) {
        return new PlatformException(CommonErrors.ACCESS_DENIED, exception);
    }
}
