package com.old.silence.core.context;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author murrayZhang
 */
public class MessageSourceAccessorHolder {

    private static MessageSourceAccessor accessor;

    private MessageSourceAccessorHolder() {
        throw new AssertionError();
    }

    public static void setAccessor(MessageSourceAccessor accessor) {
        MessageSourceAccessorHolder.accessor = accessor;
    }

    public static MessageSourceAccessor getAccessor() {
        return accessor;
    }
}
