package com.old.silence.core.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author murrayZhang
 */
public final class DateTimeUtils {

    public static final ZoneId ASIA_SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");

    private static final String DATE_VALIDATION_REGEX = "^(?:(?!0000)[0-9]{4}([/.\\-\\\\]?)(?:(?:0?[1-9]|1[0-2])\\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\\1(?:29|30)|(?:0?[13578]|1[02])\\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([/.\\-\\\\]?)0?2\\2(?:29))$"; // NOSONAR

    private static final Pattern DATE_VALIDATION_PATTERN = Pattern.compile(DATE_VALIDATION_REGEX);

    private static final String DATE_TIME_VALIDATION_REGEX = "^(?:(?!0000)[0-9]{4}([/.\\-\\\\]?)(?:(?:0?[1-9]|1[0-2])\\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\\1(?:29|30)|(?:0?[13578]|1[02])\\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([/.\\-\\\\]?)0?2\\2(?:29))\\s+(?:0?[0-9]|1[0-9]|2[0-3]):(?:0?[0-9]|[1-5][0-9]):(?:0?[0-9]|[1-5][0-9])$"; // NOSONAR

    private static final Pattern DATE_TIME_VALIDATION_PATTERN = Pattern.compile(DATE_TIME_VALIDATION_REGEX);

    private DateTimeUtils() {
        throw new AssertionError();
    }

    public static Instant toInstant(String time, String format, ZoneId zoneId) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatterCache.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return dateTime.atZone(zoneId).toInstant();
    }

    public static Instant toInstant(LocalDate date) {
        return toInstant(date, ZoneId.systemDefault());
    }

    public static Instant toInstant(LocalDate date, ZoneId zoneId) {
        return date.atStartOfDay(zoneId).toInstant();
    }

    public static ZonedDateTime toZonedDateTime(Date date) {
        return toZonedDateTime(date, ZoneId.systemDefault());
    }

    public static ZonedDateTime toUTCDateTime(Date date) {
        return toZonedDateTime(date, ZoneOffset.UTC);
    }

    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zone) {
        return date.toInstant().atZone(zone);
    }

    public static Date plus(Date date, long amountToAdd, TemporalUnit unit) {
        ZonedDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault());
        dateTime = dateTime.plus(amountToAdd, unit);
        return Date.from(dateTime.toInstant());
    }

    public static Date minus(Date date, long amountToAdd, TemporalUnit unit) {
        return plus(date, -amountToAdd, unit);
    }

    public static String format(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatterCache.ofPattern(pattern).format(temporal);
    }

    public static String formatWithUTCZone(TemporalAccessor temporal) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC).format(temporal);
    }

    public static Instant floorNow(Duration unit) {
        return floor(Instant.now(), unit);
    }

    public static Instant floor(Instant instant, Duration unit) {
        return instant.with(new TemporalRoundingAdjuster(unit, false));
    }

    public static Instant ceilNow(Duration unit) {
        return ceil(Instant.now(), unit);
    }

    public static Instant ceil(Instant instant, Duration unit) {
        return instant.with(new TemporalRoundingAdjuster(unit, true));
    }

    public static Duration getScheduledTaskInitialDelay(Duration unit) {

        Instant now = Instant.now();
        Instant ceil = ceil(now, unit);

        return Duration.between(now, ceil);
    }

    public static boolean isPositive(Duration duration) {
        return (duration.getSeconds() | duration.getNano()) > 0;
    }

    public static boolean isValidDateFormat(String str) {
        return StringUtils.isEmpty(str) || DATE_VALIDATION_PATTERN.matcher(str).matches();
    }

    public static boolean isValidDateTimeFormat(String str) {
        return StringUtils.isEmpty(str) || DATE_TIME_VALIDATION_PATTERN.matcher(str).matches();
    }

    private static class TemporalRoundingAdjuster implements TemporalAdjuster {

        private final Duration unit;

        private final boolean ceilingMode;

        public TemporalRoundingAdjuster(Duration unit, boolean ceilingMode) {
            this.unit = unit;
            this.ceilingMode = ceilingMode;
        }

        @Override
        public Temporal adjustInto(Temporal temporal) {

            long seconds = temporal.getLong(ChronoField.INSTANT_SECONDS);
            long millOfSecond = temporal.getLong(ChronoField.MILLI_OF_SECOND);

            long epochMilli = Math.multiplyExact(seconds, 1000);
            epochMilli = Math.addExact(epochMilli, millOfSecond);

            long unitInMillis = unit.toMillis();
            long dividedMilli = Math.floorDiv(epochMilli, unitInMillis);
            long roundedMilli = Math.multiplyExact(dividedMilli, unitInMillis);
            if (ceilingMode) {
                roundedMilli = Math.addExact(roundedMilli, unitInMillis);
            }

            return Instant.ofEpochMilli(roundedMilli);
        }
    }
}
