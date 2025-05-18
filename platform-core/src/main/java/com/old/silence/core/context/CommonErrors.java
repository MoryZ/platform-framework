package com.old.silence.core.context;

import com.old.silence.core.support.ErrorCoded;
import com.old.silence.core.support.ValidationErrorCode;

/**
 * @author murrayZhang
 */
public enum CommonErrors implements ErrorCodedEnumMessageSourceResolvable {

    NOT_NULL(ValidationErrorCode.MISSING_REQUIRED_PARAMETER),
    NOT_EMPTY(ValidationErrorCode.MISSING_REQUIRED_PARAMETER),
    NOT_BLANK(ValidationErrorCode.MISSING_REQUIRED_PARAMETER),
    INVALID_PARAMETER(ValidationErrorCode.INVALID_PARAMETER),
    DATE_RANGE_EXCEEDED(ValidationErrorCode.INVALID_PARAMETER),
    START_TIME_EQUAL_TO_OR_AFTER_END_TIME(ValidationErrorCode.INVALID_PARAMETER), ACCESS_DENIED(403, 1),
    DATA_NOT_EXIST(404, 1),
    COMMON_DATA_EXISTED(409, 1), PARTICULAR_DATA_EXISTED(409, 2),
    FATAL_ERROR(500, 1), SERVICE_UNAVAILABLE(503, 1),
    REMOTE_CALL_FAILED(503, 2), REMOTE_CALL_EMPTY_BODY(503, 3);

    private final int httpStatusCode;

    private final int errorCode;

    CommonErrors(ErrorCoded errorCoded) {
        this(errorCoded.getHttpStatusCode(), errorCoded.getErrorCode());
    }

    CommonErrors(int httpStatusCode, int errorCode) {
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
    }

    @Override
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }
}
