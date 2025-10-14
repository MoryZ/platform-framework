package com.old.silence.webmvc.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
public class GlobalMvcExceptionHandler extends ResponseEntityExceptionHandler {

    private final MvcExceptionHandlerFactory handlerFactory;

    private final String systemIdentifier;

    public GlobalMvcExceptionHandler(MvcExceptionHandlerFactory handlerFactory, String systemIdentifier) {
        this.handlerFactory = handlerFactory;
        this.systemIdentifier = systemIdentifier;
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return (ResponseEntity)handleThrowable((Throwable)e, request);
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<?> handleThrowable(Throwable t, WebRequest request) {
        MvcExceptionHandler exceptionHandler = this.handlerFactory.getExceptionHandler((Class)t.getClass());
        return exceptionHandler.handle(t, request, this.systemIdentifier);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = e.getMessage();
        if (status.is5xxServerError())
            return (ResponseEntity)handleThrowable(e, request);
        if (StringUtils.isNotEmpty(message))
            this.logger.warn(message);
        return super.handleExceptionInternal(e, body, headers, status, request);
    }

}
