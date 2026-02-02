package com.old.silence.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * @author murrayZhang
 */
public abstract class NullableUnwrapperConverters {

    /**
     * Registers converters for unwrapper types found on the classpath.
     *
     * @param registry must not be {@literal null}.
     */
    public static void registerConvertersIn(ConverterRegistry registry) {

        Objects.requireNonNull(registry, "ConverterRegistry must not be null");

        registry.addConverter(Jdk8OptionalUnwrapper.INSTANCE);
    }

    public static Collection<?> getConvertersToRegister() {

        List<Object> converters = new ArrayList<>();
        converters.add(Jdk8OptionalUnwrapper.INSTANCE);

        return converters;
    }

    private enum Jdk8OptionalUnwrapper implements GenericConverter {

        INSTANCE;

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(Optional.class, Object.class));
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return ((Optional<?>) source).orElse(null);
        }
    }

}
