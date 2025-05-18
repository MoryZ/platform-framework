package com.old.silence.core.enums;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * @author murrayZhang
 */
public class EnumValueSerializer extends StdScalarSerializer<EnumValue<?>> {

    private static final long serialVersionUID = -1635127536227621036L;

    public EnumValueSerializer() {
        super(EnumValue.class, false);
    }

    @Override
    public void serialize(EnumValue<?> enumValue, JsonGenerator generator, SerializerProvider provider) throws IOException {

        if (enumValue == null) {
            return;
        }

        Object value = enumValue.getValue();
        JsonSerializer<Object> valueSerializer = provider.findValueSerializer(value.getClass());
        valueSerializer.serialize(value, generator, provider);
    }
}
