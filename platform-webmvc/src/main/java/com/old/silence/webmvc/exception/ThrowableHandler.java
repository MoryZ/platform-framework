package com.old.silence.webmvc.exception;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.core.context.CommonErrors;
import com.old.silence.web.data.RestErrorResponse;

public class ThrowableHandler extends AbstractMvcExceptionHandler {

    public ThrowableHandler(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor, Throwable.class);
    }

    @Override
    public ResponseEntity<?> handle(Throwable exception, WebRequest request, String systemIdentifier) {

        logger.error("Caught unknown exception of {}", request, exception);

        String message = messageSourceAccessor.getMessage(CommonErrors.FATAL_ERROR);
        return RestErrorResponse.createErrorResponseEntity(CommonErrors.FATAL_ERROR, systemIdentifier, message);
    }
}
