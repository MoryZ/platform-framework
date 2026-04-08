package com.old.silence.core.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author moryZhang
 */
@JsonSerialize(using = EnumValueSerializer.class)
@JsonDeserialize(using = EnumValueDeserializer.class)
public interface EnumValue<T> {

    T getValue();
}

