package com.old.silence.http.converter.support;

@FunctionalInterface
public interface CsvFilenameGenerator {

    String generate(Class<?> type);
}
