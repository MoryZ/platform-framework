package com.old.silence.core.support;

import com.old.silence.core.exception.PlatformException;

/**
 * @author murrayZhang
 */
public interface ErrorCoded {

    String DEFAULT_PREFIX = "EC";

    int DEFAULT_VERSION = 1;

    default String getPrefix() {
        return DEFAULT_PREFIX;
    }

    default int getVersion() {
        return DEFAULT_VERSION;
    }

    default ErrorLevel getLevel() {
        return getHttpStatusCode() == 500 ? ErrorLevel.FATAL : ErrorLevel.ERROR;
    }

    default ErrorType getType() {
        int httpStatus = getHttpStatusCode();
        if (httpStatus == 500) {
            return ErrorType.SYSTEM;
        } else if (httpStatus == 503) {
            return ErrorType.EXTERNAL_SERVICE;
        } else {
            return ErrorType.BUSINESS;
        }
    }

    int getHttpStatusCode();

    int getErrorCode();

    default String generateErrorCode(String systemIdentifier) {

        StringBuilder builder = new StringBuilder(13);
        builder.append(getPrefix());
        builder.append(getVersion());
        builder.append(getLevel().getCode());
        builder.append(getType().getCode());
        builder.append(systemIdentifier);
        builder.append(getHttpStatusCode());
        int errorCode = getErrorCode();
        if (errorCode < 10) {
            builder.append('0');
        }
        builder.append(errorCode);

        return builder.toString();
    }

    default PlatformException createException() {
        return new PlatformException(this);
    }

    enum ErrorLevel {

        WARNING('W'), ERROR('E'), FATAL('F');

        private final char code;

        ErrorLevel(char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    enum ErrorType {

        BUSINESS('B'), SYSTEM('S'), EXTERNAL_SERVICE('T');

        private final char code;

        ErrorType(char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }
}
