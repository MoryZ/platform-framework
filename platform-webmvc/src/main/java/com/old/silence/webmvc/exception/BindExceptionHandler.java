package com.old.silence.webmvc.exception;

import java.util.Optional;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.core.context.CommonErrors;
import com.old.silence.core.support.ErrorCoded;
import com.old.silence.core.support.ValidationErrorCode;
import com.old.silence.validation.util.ValidationUtils;
import com.old.silence.web.data.RestErrorResponse;

public class BindExceptionHandler extends AbstractMvcExceptionHandler {

    public BindExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor, BindException.class);
    }

    @Override
    public ResponseEntity<?> handle(Throwable bindException, WebRequest request, String systemIdentifier) {

        BindException exception = (BindException) bindException;
        ErrorCoded errorCode = ValidationErrorCode.INVALID_PARAMETER;
        String message;
        if (exception.hasFieldErrors()) {
            FieldError fieldError = exception.getFieldError();
            String code = Optional.ofNullable(fieldError).map(FieldError::getCode).orElse("");
            if (StringUtils.equalsAny(code, TypeMismatchException.ERROR_CODE, MethodInvocationException.ERROR_CODE)) {
                MessageSourceResolvable messageSourceResolvable = CommonErrors.INVALID_PARAMETER
                        .toResolvableWithArguments(fieldError.getArguments()); // NOSONAR: already checked exception.hasFieldErrors()
                message = messageSourceAccessor.getMessage(messageSourceResolvable);
            } else {
                FieldError error = exception.getFieldError();
                if (error != null && error.contains(ConstraintViolation.class)) {
                    ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
                    errorCode = ValidationUtils.extractErrorCode(violation);
                }
                message = messageSourceAccessor.getMessage(exception.getFieldError());
            }
        } else {
            ObjectError error = exception.getAllErrors().get(0);
            message = error.getDefaultMessage();
        }

        return RestErrorResponse.createErrorResponseEntity(errorCode, systemIdentifier, message);
    }

}
