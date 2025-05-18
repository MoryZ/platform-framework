package com.old.silence.core.context;

import org.apache.commons.lang3.ArrayUtils;
import com.old.silence.core.exception.PlatformException;
import com.old.silence.core.support.ErrorCoded;

/**
 * @author murrayZhang
 */
public interface ErrorCodedEnumMessageSourceResolvable extends EnumMessageSourceResolvable, ErrorCoded {

    @Override
    default PlatformException createException() {
        return createException(ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    default PlatformException createException(Object... args) {
        return new PlatformException(this, args);
    }
}
