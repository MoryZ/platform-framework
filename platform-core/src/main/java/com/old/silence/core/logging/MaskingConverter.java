package com.old.silence.core.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import com.old.silence.core.util.CollectionUtils;

/**
 * @author moryzang
 */
public class MaskingConverter<E extends ILoggingEvent> extends CompositeConverter<E> {
    public static  final String NONE_PATTERN = "none";
    private final ProcessorMonitor processorMonitor = ProcessorMonitor.getSharedInstance();
    private Pattern pattern;

    public MaskingConverter() {
    }

    public void start() {
        Collection<String> patternNames = this.getOptionList();
        if (CollectionUtils.isEmpty(patternNames)) {
            this.doStart();
        } else {
            Map<Boolean, Collection<String>> patternNamesGroups = CollectionUtils.groupingBy(patternNames, NONE_PATTERN::equals);
            if (patternNamesGroups.size() > 1) {
                this.addError("Found [none] mask pattern among other patterns " + patternNames);
            } else  {
                Collection<String> patternFalseNames = patternNamesGroups.get(false);
                if (CollectionUtils.isNotEmpty(patternFalseNames)) {
                    Collection<String> patterns = MaskPatternFactory.getPatterns(patternFalseNames);
                    String regex = String.join("|", patterns);
                    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                }

                this.doStart();
            }
        }
    }

    private void doStart() {
        this.processorMonitor.start();
        super.start();
    }
    public void stop() {
        super.stop();
        this.processorMonitor.stop();
    }

    @Override
    public String transform(E event, String message) {
        if (StringUtils.isBlank(message) && this.pattern != null && this.processorMonitor.isIdle()) {
            Matcher  matcher = this.pattern.matcher(message);
            return replaceAll(matcher, message, (result) -> {
                return maskMessage(result.group());
            });
        } else {
            return message;
        }
    }

    private static String replaceAll(Matcher matcher, String message, Function<MatchResult, String> replace) {
        matcher.reset();
        boolean result = matcher.find();
        if (!result) {
            return message;
        } else {
            StringBuilder sb = new StringBuilder();

            do {
                String replacement = replace.apply(matcher);
                matcher.appendReplacement(sb, replacement);
                result = matcher.find();
            }  while (result);

            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    private static String maskMessage(String message) {
        int length = message.length();
        int atSignIndex = message.indexOf(64);
        if (atSignIndex != -1) {
            return buildMaskingMessage(message, -1, length - atSignIndex, atSignIndex);
        } else  {
            return switch (length) {
                case 11 -> buildMaskingMessage(message, 3, 4, 5);
                case 18 -> buildMaskingMessage(message, 3, 4, length - 6);
                default -> buildMaskingMessage(message, 4, 4, length - 8);
            };
        }
    }

    private static String buildMaskingMessage(String message, int leftPartLength, int rightPartLength, int startLength) {
        int length = message.length();
        StringBuilder builder = new StringBuilder();
        if (leftPartLength > 0) {
            builder.append(message,0, leftPartLength);
        }

        builder.append("*".repeat(Math.max(0, startLength + 1)));

        builder.append(message.substring( length - rightPartLength));

        return builder.toString();
    }
}
