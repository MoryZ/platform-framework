package com.old.silence.core.exception;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.IntPredicate;

import org.apache.commons.lang3.StringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.old.silence.core.context.CommonErrors;
import com.old.silence.core.context.MessageSourceAccessorHolder;

/**
 * @author moryzang
 */
class PlatformExceptionTest {
    private static final IntPredicate CHINESE_CHARACTER_PREDICATE = codePoint -> Character.UnicodeScript.of(codePoint)
            == Character.UnicodeScript.HAN;

    PlatformExceptionTest() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setBasenames("messages/platform_common_messages");
        messageSource.setFallbackToSystemLocale(false);

        var messageSourceAccessor = new MessageSourceAccessor(messageSource);
        MessageSourceAccessorHolder.setAccessor(messageSourceAccessor);
    }

    @Test
    void testGetMessage() {
        for (var error : CommonErrors.values()) {
            var exception = new PlatformException(error);

            String message = exception.getMessage(Locale.ENGLISH);
            assertThat(message).isNotBlank();
            assertThat(message.codePoints().anyMatch(CHINESE_CHARACTER_PREDICATE)).isFalse();

            message = exception.getMessage(Locale.CHINA);
            assertThat(message).isNotBlank();
            assertThat(message.codePoints().anyMatch(CHINESE_CHARACTER_PREDICATE)).isTrue();

        }
    }

    @Test
    void testGetMessageWithArguments() {
        Object[] args = {"Id"};
        var exception = new PlatformException(CommonErrors.NOT_BLANK, args);

        var message = exception.getMessage(Locale.ENGLISH);
        assertThat(message).isNotBlank();
        assertThat(StringUtils.containsAny(message, "{", "}")).isFalse();
        assertThat(message.codePoints().anyMatch(CHINESE_CHARACTER_PREDICATE)).isFalse();

        message = exception.getMessage(Locale.CHINA);
        assertThat(message).isNotBlank();
        assertThat(StringUtils.containsAny(message, "{", "}")).isFalse();
        assertThat(message.codePoints().anyMatch(CHINESE_CHARACTER_PREDICATE)).isTrue();

    }
}
