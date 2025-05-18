package com.old.silence.core.context;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import com.old.silence.core.util.CaseUtils;

/**
 * @author murrayZhang
 */
public interface EnumMessageSourceResolvable extends MessageSourceResolvable, Serializable {

    ConcurrentMap<EnumMessageSourceResolvable, String> MESSAGE_CODE_NAME_CACHE = new ConcurrentHashMap<>(); // NOSONAR

    String name();

    @Override
    default String[] getCodes() {

        String code = MESSAGE_CODE_NAME_CACHE.computeIfAbsent(this,
                messageCode -> StringUtils.substringBefore(ClassUtils.getShortName(messageCode.getClass()), ".") + '.'
                        + CaseUtils.toCamelCase(messageCode.name(), true, '_'));
        return new String[] { code };
    }

    default MessageSourceResolvable toResolvableWithArguments(Collection<Object> args) {
        return CollectionUtils.isEmpty(args) ? this : toResolvableWithArguments(args.toArray());
    }

    default MessageSourceResolvable toResolvableWithArguments(Object... args) {

        if (ArrayUtils.isEmpty(args)) {
            return this;
        }

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Number) {
                args[i] = arg.toString();
            }
        }
        return new DefaultMessageSourceResolvable(getCodes(), args, null);
    }
}
