package com.old.silence.webmvc.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

public interface MvcExceptionHandler {
    Class<? extends Throwable> getSupportedExceptionType();

    ResponseEntity<?> handle(Throwable exception, WebRequest request, String systemIdentifier);
}
