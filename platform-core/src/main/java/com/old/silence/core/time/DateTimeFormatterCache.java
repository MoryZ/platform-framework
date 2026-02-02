package com.old.silence.core.time;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author murrayZhang
 */
public class DateTimeFormatterCache {

    private static final ConcurrentMap<String, DateTimeFormatter> CACHE = new ConcurrentHashMap<>();

    private DateTimeFormatterCache() {
        throw new AssertionError();
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }
}
