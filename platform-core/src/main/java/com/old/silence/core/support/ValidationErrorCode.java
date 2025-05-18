package com.old.silence.core.support;

/**
 * @author murrayZhang
 */
public enum ValidationErrorCode implements ErrorCoded {

    MISSING_REQUIRED_PARAMETER(1), INVALID_PARAMETER(2);

    private final int errorCode;

    ValidationErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public int getHttpStatusCode() {
        return 400;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }
}
