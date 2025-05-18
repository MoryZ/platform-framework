package com.old.silence.json.data;

import java.io.IOException;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class PageSerializer extends JsonSerializer<IPage<Object>> {
    @Override
    public void serialize(IPage<Object> page, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("total", page.getTotal());
        gen.writeObjectField("data", page.getRecords());
        gen.writeEndObject();
    }
}
