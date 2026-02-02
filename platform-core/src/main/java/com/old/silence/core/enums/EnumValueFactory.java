package com.old.silence.core.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import com.old.silence.core.enums.util.EnumValueUtils;

/**
 * @author murrayZhang
 */
public class EnumValueFactory {

    private static final ConversionService CONVERSION_SERVICE = DefaultConversionService.getSharedInstance();

    private static final ConcurrentMap<Class<? extends EnumValue<?>>, Map<Object, EnumValue<?>>> ENUM_CACHE = new ConcurrentHashMap<>();

    private EnumValueFactory() {
        throw new AssertionError();
    }

    public static Map<Object, EnumValue<?>> getEnumValueMap(Class<? extends EnumValue<?>> enumValueClass) {
        return ENUM_CACHE.computeIfAbsent(enumValueClass, clazz -> {
            EnumValue<?>[] enumConstants = clazz.getEnumConstants();
            Map<Object, EnumValue<?>> enumMap = new HashMap<>();
            for (EnumValue<?> enumValue : enumConstants) {
                enumMap.put(enumValue.getValue(), enumValue);
            }
            return Collections.unmodifiableMap(enumMap);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumValue<?>> Optional<T> get(Class<T> enumValueClass, Object value) {
        if (value == null) {
            return Optional.empty();
        }
        Class<?> valueType = EnumValueUtils.getEnumValueActualType(enumValueClass);
        if (valueType != value.getClass()) {
            value = CONVERSION_SERVICE.convert(value, valueType);
        }
        return Optional.ofNullable((T) getEnumValueMap(enumValueClass).get(value));
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumValue<?>> Optional<T> getWithoutConversion(Class<T> enumValueClass, Object value) {
        return value == null ? Optional.empty() : Optional.ofNullable((T) getEnumValueMap(enumValueClass).get(value));
    }

    public static <T extends EnumValue<?>> T getRequired(Class<T> enumValueClass, Object value) {
        return get(enumValueClass, value).orElseThrow(() -> new IllegalArgumentException(
                String.format("Can not find %s of enum value type %s", value, enumValueClass)));
    }
}
