package com.old.silence.core.enums;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.old.silence.core.enums.util.EnumValueUtils;

/**
 * @author murrayZhang
 */
public class EnumValueDeserializer extends StdScalarDeserializer<Object> implements ContextualDeserializer {

    private static final long serialVersionUID = 4179033925074547470L;

    private Class<? extends EnumValue<?>> enumValueClass;

    private Class<?> valueType;

    public EnumValueDeserializer() {
        super(EnumValue.class);
    }

    public EnumValueDeserializer(Class<? extends EnumValue<?>> enumValueClass) {
        super(enumValueClass);
        this.enumValueClass = enumValueClass;
        this.valueType = EnumValueUtils.getEnumValueActualType(enumValueClass);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        @SuppressWarnings("unchecked")
        Class<? extends EnumValue<?>> enumValueClass = (Class<? extends EnumValue<?>>) ctxt.getContextualType().getRawClass();
        return new EnumValueDeserializer(enumValueClass);
    }

    @Override
    public EnumValue<?> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {

        Object value = null;
        if (parser.isExpectedStartObjectToken()) {
            JsonNode node = (JsonNode) parser.readValueAsTree();
            value = node.get("value").asText();
        } else {
            value = parser.readValueAs(valueType);
        }
        return EnumValueFactory.get(enumValueClass, value).orElse(null);
    }
}
