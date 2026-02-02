package com.old.silence.core.util;

import java.util.Optional;
import java.util.function.BinaryOperator;

/**
 * @author murrayZhang
 */
public final class OptionalUtils {

    private OptionalUtils() {
        throw new AssertionError();
    }

    public static <T> Optional<T> mapIfAllPresentOrElse(Optional<T> left, Optional<T> right, BinaryOperator<T> function) {
        return left.map(l -> Optional.of(right.map(r -> function.apply(l, r)).orElse(l))).orElse(right);
    }
}
