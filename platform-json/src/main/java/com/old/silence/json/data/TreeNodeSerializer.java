package com.old.silence.json.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.old.silence.dto.TreeDto;

import java.io.IOException;

class TreeNodeSerializer extends JsonSerializer<TreeDto> {
    @Override
    public void serialize(TreeDto treeDto, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("key", treeDto.getId());
        gen.writeObjectField("title", treeDto.getName());
        gen.writeObjectField("children", treeDto.getChildren());
        gen.writeEndObject();
    }
}
