package com.old.silence.csv;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@FunctionalInterface
public interface Jackson2CsvMapperBuilderCustomizer {

    void customize(Jackson2ObjectMapperBuilder builder);
}
