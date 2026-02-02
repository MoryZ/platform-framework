package com.old.silence.webmvc.exception;

import java.util.Locale;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.core.exception.PlatformException;
import com.old.silence.web.data.RestErrorResponse;

public class PlatformExceptionHandler extends AbstractMvcExceptionHandler {

    public PlatformExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor, PlatformException.class);
    }

    public PlatformExceptionHandler(MessageSourceAccessor messageSourceAccessor, boolean warningExceptionLoggingRequired) {
        super(messageSourceAccessor, PlatformException.class, warningExceptionLoggingRequired);
    }

    @Override
    public ResponseEntity<?> handle(Throwable platfromException, WebRequest request, String systemIdentifier) {

        PlatformException exception = (PlatformException) platfromException;
        logException(HttpStatus.valueOf(exception.getHttpStatusCode()), exception.getMessage(Locale.ENGLISH), exception);
        return RestErrorResponse.createErrorResponseEntity(exception, systemIdentifier);
    }
}
