package com.old.silence.core.test.context;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntPredicate;

import org.apache.commons.lang3.StringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.GenericTypeResolver;
import com.old.silence.core.context.EnumMessageSourceResolvable;
import com.old.silence.core.test.UnitTests;

/**
 * @author moryzang
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractEnumMessageSourceResolvableTests<T extends EnumMessageSourceResolvable> extends UnitTests {
    protected static final IntPredicate CHINESE_CHARACTER_PREDICATE = codePoint -> Character.UnicodeScript.of(codePoint)
            == Character.UnicodeScript.HAN;
    protected final MessageSourceAccessor messageSourceAccessor;
    protected final Class<T> type;
    private final Map<T, Collection<Object>> argumentsMap;

    @SuppressWarnings("unchecked")
    protected AbstractEnumMessageSourceResolvableTests(String... messageSourceBasenames) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setBasenames(messageSourceBasenames);
        messageSource.setFallbackToSystemLocale(false);
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
        this.type = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractEnumMessageSourceResolvableTests.class);
        this.argumentsMap = this.createArgumentsMap();
    }

    protected Map<T, Collection<Object>> createArgumentsMap() {
        return Collections.emptyMap();
    }

    @Test
    void testGetMessage() {
        T[] enumResolvables = this.type.getEnumConstants();
        for (var enumResolvable : enumResolvables) {

            Collection<Object> arguments = argumentsMap.get(enumResolvable);
            MessageSourceResolvable resolvable;
            if (arguments == null) {
                resolvable = enumResolvable;
            } else {
                resolvable = enumResolvable.toResolvableWithArguments(arguments);
            }

            String message = messageSourceAccessor.getMessage(resolvable, Locale.ENGLISH);
            assertThat(message).isNotBlank();
            assertThat(StringUtils.containsAny(message, "{", "}")).isFalse();
            assertThat(message.codePoints().anyMatch(CHINESE_CHARACTER_PREDICATE)).isFalse();

            message = messageSourceAccessor.getMessage(resolvable, Locale.CHINA);
            assertThat(message).isNotBlank();
            assertThat(StringUtils.containsAny(message, "{", "}")).isFalse();
            assertThat(message.codePoints().anyMatch(CHINESE_CHARACTER_PREDICATE)).isTrue();


        }
    }
}
