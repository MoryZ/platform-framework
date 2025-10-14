package com.old.silence.webmvc.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;


import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.validation.util.ValidationUtils;
import com.old.silence.web.data.RestErrorResponse;

public class ConstraintViolationExceptionHandler extends AbstractMvcExceptionHandler {

    public ConstraintViolationExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor, ConstraintViolationException.class);
    }

    @Override
    public ResponseEntity<?> handle(Throwable constraintViolationException, WebRequest request, String systemIdentifier) {

        ConstraintViolationException exception = (ConstraintViolationException) constraintViolationException;
        ConstraintViolation<?> violation = exception.getConstraintViolations().iterator().next();
        String message = violation.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = ValidationUtils.getViolationMessage(violation, messageSourceAccessor);
        }
        return RestErrorResponse.createErrorResponseEntity(ValidationUtils.extractErrorCode(violation), systemIdentifier,
                message);
    }
}
