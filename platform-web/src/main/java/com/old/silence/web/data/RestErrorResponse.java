package com.old.silence.web.data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.old.silence.core.exception.PlatformException;
import com.old.silence.core.support.ErrorCoded;

public class RestErrorResponse {
    private final String code;
    private final String message;

    public RestErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public static ResponseEntity<RestErrorResponse> createErrorResponseEntity(PlatformException exception, String systemIdentifier) {
        return createErrorResponseEntity((ErrorCoded)exception, systemIdentifier, (String)exception.getMessage());
    }
    public static ResponseEntity<RestErrorResponse> createErrorResponseEntity(ErrorCoded errorCoded, String systemIdentifier, String message) {
        return createErrorResponseEntity(errorCoded.generateErrorCode(systemIdentifier), message, errorCoded.getHttpStatusCode());
    }

    public static ResponseEntity<RestErrorResponse> createErrorResponseEntity(String code, String message, int httpStatusCode) {
        return createErrorResponseEntity(code, message, HttpStatus.valueOf(httpStatusCode));
    }

    public static ResponseEntity<RestErrorResponse> createErrorResponseEntity(String code, String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new RestErrorResponse(code, message), httpStatus);
    }
}
