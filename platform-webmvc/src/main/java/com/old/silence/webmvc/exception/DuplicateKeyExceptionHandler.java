package com.old.silence.webmvc.exception;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.context.request.WebRequest;
import com.old.silence.core.context.CommonErrors;
import com.old.silence.core.exception.PlatformException;

public class DuplicateKeyExceptionHandler extends PlatformExceptionHandlerDelegatedExceptionHandler {

    public DuplicateKeyExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor, DuplicateKeyException.class);
    }

    @Override
    protected PlatformException createPlatformException(Throwable exception, WebRequest request) {
        return new PlatformException(CommonErrors.COMMON_DATA_EXISTED, exception);
    }
}
