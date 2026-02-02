package com.old.silence.core.enums.converter;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import com.old.silence.core.enums.EnumValue;
import com.old.silence.core.enums.EnumValueFactory;
import com.old.silence.core.enums.util.EnumValueUtils;

/**
 * @author murrayZhang
 */
public class EnumValueConverter implements ConditionalGenericConverter {

    private static final TypeDescriptor ENUM_TYPE = TypeDescriptor.valueOf(Enum.class);

    private static final TypeDescriptor ENUM_VALUE_TYPE = TypeDescriptor.valueOf(EnumValue.class);

    private final ConversionService conversionService;

    public EnumValueConverter() {
        this(DefaultConversionService.getSharedInstance());
    }

    public EnumValueConverter(Optional<ConversionService> conversionService) {
        this(conversionService.orElseGet(DefaultConversionService::new));
    }

    public EnumValueConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return null;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(ENUM_TYPE) || targetType.isAssignableTo(ENUM_TYPE)) {
            return sourceType.isAssignableTo(ENUM_VALUE_TYPE) || targetType.isAssignableTo(ENUM_VALUE_TYPE);
        } else {
            return false;
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        if (source == null) {
            return null;
        }

        if (Objects.equals(sourceType, targetType)) {
            return source;
        }

        if (sourceType.isAssignableTo(ENUM_VALUE_TYPE)) {
            Object value = ((EnumValue<?>) source).getValue();
            TypeDescriptor valueType = TypeDescriptor.forObject(value);
            if (valueType.isAssignableTo(targetType)) { // NOSONAR
                return value;
            } else {
                return conversionService.convert(value, valueType, targetType);
            }
        }

        if (targetType.isAssignableTo(ENUM_VALUE_TYPE)) {
            Class<EnumValue<?>> enumValueClass = (Class<EnumValue<?>>) targetType.getType();
            TypeDescriptor valueType = TypeDescriptor.valueOf(EnumValueUtils.getEnumValueActualType(enumValueClass));
            if (valueType.equals(sourceType)) {
                return EnumValueFactory.getWithoutConversion(enumValueClass, source).orElse(null);
            } else {
                Object value = conversionService.convert(source, sourceType, valueType);
                return EnumValueFactory.getWithoutConversion(enumValueClass, value).orElse(null);
            }
        }

        // Should not happen
        throw new IllegalStateException("Unexpected source/target types");
    }
}
