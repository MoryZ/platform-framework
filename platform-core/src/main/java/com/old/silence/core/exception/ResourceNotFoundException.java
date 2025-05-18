package com.old.silence.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.old.silence.core.context.CommonErrors;

/**
 * @author murrayZhang
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends PlatformException {

    private static final long serialVersionUID = 2995181556141851952L;

    public ResourceNotFoundException() {
        this(CommonErrors.DATA_NOT_EXIST.getErrorCode(), null);
    }

    public ResourceNotFoundException(String message) {
        this(CommonErrors.DATA_NOT_EXIST.getErrorCode(), message);
    }

    public ResourceNotFoundException(int errorCode) {
        this(errorCode, null);
    }

    public ResourceNotFoundException(int errorCode, String message) {
        super(404, errorCode, message);
    }
}
