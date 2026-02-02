package com.old.silence.http.converter.support;

import java.time.LocalDateTime;

import com.old.silence.core.time.DateTimeUtils;


public class DefaultCsvFilenameGenerator implements CsvFilenameGenerator{

    public DefaultCsvFilenameGenerator() {
    }

    @Override
    public String generate(Class<?> type) {
        return type.getSimpleName() + '_' + DateTimeUtils.format(LocalDateTime.now(), "yyyyMMddHHmmss") + ".csv";
    }
}
