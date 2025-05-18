package com.old.silence.csv;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

class CsvBigDecimalSerializer extends StdScalarSerializer<BigDecimal> {

    private static final long serialVersionUID = 3531547778960189527L;

    public CsvBigDecimalSerializer() {
        super(BigDecimal.class, false);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.stripTrailingZeros().toPlainString());
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("number", true);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitFloatFormat(visitor, typeHint, JsonParser.NumberType.BIG_DECIMAL);
    }
}
