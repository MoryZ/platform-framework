package com.old.silence.core.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * @author moryzang
 */
class MaskingConverterTest {
    private static final String MESSAGE = "My phone is 13611988536, email is 'HEXIAN001@gmail.com',id_card is 3101111199001012022, bank_card is 6228481848093871375, blablabla";

    @Test
    void testTransform() {
        MaskingConverter<ILoggingEvent> converter = new MaskingConverter<>();
        converter.setOptionList(Arrays.asList("phone", "email", "id_card", "bank_card"));
        converter.start();

        assertThat(converter.isStarted()).isTrue();

        String maskedMessage = converter.transform(new LoggingEvent(), MESSAGE);
        String expectedMaskedMessage = "My phone is 136****8536, email is '*********@gmail.com',id_card is 310*************022, bank_card is 6228***********1375, blablabla";
        assertThat(maskedMessage).isEqualTo(expectedMaskedMessage);
    }

    @Test
    void testTransformWithEmptyOptions() {
        MaskingConverter<ILoggingEvent> converter = new MaskingConverter<>();
        converter.start();

        assertThat(converter.isStarted()).isTrue();

        String maskedMessage = converter.transform(new LoggingEvent(), MESSAGE);
        assertThat(maskedMessage).isEqualTo(MESSAGE);
    }

    @Test
    void testTransformWithNonePattern() {
        MaskingConverter<ILoggingEvent> converter = new MaskingConverter<>();
        converter.setOptionList(Collections.singletonList(MaskingConverter.NONE_PATTERN));
        converter.start();

        assertThat(converter.isStarted()).isTrue();

        String maskedMessage = converter.transform(new LoggingEvent(), MESSAGE);
        assertThat(maskedMessage).isEqualTo(MESSAGE);
    }

    @Test
    void testTransformWithEmptyMessage() {
        MaskingConverter<ILoggingEvent> converter = new MaskingConverter<>();
        converter.setOptionList(Arrays.asList("phone", "email", "id_card", "bank_card"));
        converter.start();

        assertThat(converter.isStarted()).isTrue();

        String message = "";
        String maskedMessage = converter.transform(new LoggingEvent(), message);
        assertThat(maskedMessage).isEqualTo(message);
    }
}
