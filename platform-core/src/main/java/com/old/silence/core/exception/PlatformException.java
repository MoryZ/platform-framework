package com.old.silence.core.exception;

import static com.old.silence.core.context.MessageSourceAccessorHolder.getAccessor;

import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import com.old.silence.core.context.ErrorCodedEnumMessageSourceResolvable;
import com.old.silence.core.support.ErrorCoded;

/**
 * @author murrayZhang
 */
public class PlatformException extends RuntimeException implements ErrorCoded {

    private static final long serialVersionUID = -813045483381248742L;

    private static final int[] ALLOWED_ERROR_HTTP_STATUS_CODES = { 400, 401, 403, 404, 409, 422, 423, 500, 503 };

    private final int httpStatusCode;

    private final int errorCode;

    private final transient MessageSourceResolvable messageSourceResolvable;

    public PlatformException(ErrorCoded errorCoded) {
        this(errorCoded, (String) null);
    }

    public PlatformException(ErrorCoded errorCoded, String message) {
        this(errorCoded.getHttpStatusCode(), errorCoded.getErrorCode(), message);
    }

    public PlatformException(ErrorCoded errorCoded, Throwable cause) {
        this(errorCoded.getHttpStatusCode(), errorCoded.getErrorCode(), cause);
    }

    public PlatformException(ErrorCoded errorCoded, String message, Throwable cause) {
        this(errorCoded.getHttpStatusCode(), errorCoded.getErrorCode(), message, cause);
    }

    protected PlatformException(int httpStatusCode, int errorCode, String message) {
        this(httpStatusCode, errorCode, message, null);
    }

    protected PlatformException(int httpStatusCode, int errorCode, Throwable cause) {
        this(httpStatusCode, errorCode, (String) null, cause);
    }

    protected PlatformException(int httpStatusCode, int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = validateHttpStatusCode(httpStatusCode);
        this.errorCode = validateErrorCode(errorCode);
        this.messageSourceResolvable = null;
    }

    public PlatformException(ErrorCodedEnumMessageSourceResolvable messageSourceResolvable) {
        this(messageSourceResolvable, null, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public PlatformException(ErrorCodedEnumMessageSourceResolvable messageSourceResolvable, Object... args) {
        this(messageSourceResolvable, null, args);
    }

    public PlatformException(ErrorCodedEnumMessageSourceResolvable messageSourceResolvable, Throwable cause) {
        this(messageSourceResolvable, cause, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public PlatformException(ErrorCodedEnumMessageSourceResolvable messageSourceResolvable, Throwable cause, Object... args) {
        this(messageSourceResolvable, messageSourceResolvable.toResolvableWithArguments(args), cause);
    }

    public PlatformException(ErrorCoded errorCoded, MessageSourceResolvable resolvable) {
        this(errorCoded.getHttpStatusCode(), errorCoded.getErrorCode(), resolvable, null);
    }

    public PlatformException(ErrorCoded errorCoded, MessageSourceResolvable resolvable, Throwable cause) {
        this(errorCoded.getHttpStatusCode(), errorCoded.getErrorCode(), resolvable, cause);
    }

    protected PlatformException(int httpStatusCode, int errorCode, MessageSourceResolvable messageSourceResolvable, Throwable cause) {
        super(cause);
        this.httpStatusCode = validateHttpStatusCode(httpStatusCode);
        this.errorCode = validateErrorCode(errorCode);
        this.messageSourceResolvable = messageSourceResolvable;
    }

    @Override
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    public MessageSourceResolvable getMessageSourceResolvable() {
        return messageSourceResolvable;
    }

    @Override
    public String getMessage() {
        return getMessage(null);
    }

    public String getMessage(Locale locale) {
        if (messageSourceResolvable == null || getAccessor() == null) {
            return super.getMessage();
        }
        if (locale == null) {
            locale = LocaleContextHolder.getLocale();
        }
        return getAccessor().getMessage(messageSourceResolvable, locale);
    }

    private static int validateHttpStatusCode(int httpStatusCode) {

        if (!ArrayUtils.contains(ALLOWED_ERROR_HTTP_STATUS_CODES, httpStatusCode)) {
            throw new IllegalArgumentException("Http status code [" + httpStatusCode + "] is not allowed");
        }

        return httpStatusCode;
    }

    private static int validateErrorCode(int errorCode) {

        if (errorCode < 0 || errorCode >= 100) {
            throw new IllegalArgumentException("Invalid error code [" + errorCode + "]");
        }

        return errorCode;
    }
}

