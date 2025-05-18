package com.old.silence.csv;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase;

public class CsvInstantSerializer extends InstantSerializerBase<Instant> { // NOSONAR

    private static final long serialVersionUID = 2332834179093828460L;

    public static final CsvInstantSerializer INSTANCE = new CsvInstantSerializer();

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    protected CsvInstantSerializer() {
        super(Instant.class, Instant::toEpochMilli, Instant::getEpochSecond, Instant::getNano,
                DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    protected CsvInstantSerializer(CsvInstantSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, null, formatter);
    }

    protected CsvInstantSerializer(CsvInstantSerializer base, Boolean useTimestamp, Boolean useNanoseconds,
                                   DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    @Override
    protected CsvInstantSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new CsvInstantSerializer(this, useTimestamp, formatter);
    }

    @Override
    protected CsvInstantSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new CsvInstantSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }

}
