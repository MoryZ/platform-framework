package com.old.silence.core.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author moryzang
 */
final class MaskPatternFactory {
    private static final String MASK_PATTERN_FILE_PATH = "platform-logging-mask-patterns.properties";
    private static final Map<String, String> PATTERNS;

    private MaskPatternFactory() {
        throw new AssertionError();
    }

    static Collection<String> getPatterns(Collection<String> patternNames) {
        List<String>  patterns = new ArrayList<>();
        var iterator = PATTERNS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (patternNames.contains(entry.getKey())) {
                patterns.add((String) entry.getValue());
            }
        }
        return patterns;
    }

    static {
        try {
            Resource  resource = new ClassPathResource(MASK_PATTERN_FILE_PATH);
            var reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            Map patterns;
            try {
                Stream<String> lines = reader.lines();

                try {
                    patterns = lines.collect(Collectors.toMap((line) -> {
                        return StringUtils.substringBefore(line, "=");
                    }, line -> {
                        return StringUtils.substringAfter(line, "=");
                    }, (l, r) -> {
                        return l;
                    }, LinkedHashMap::new ));
                } catch (Exception e) {
                    if (lines != null) {
                        try {
                            lines.close();
                        }  catch (Throwable e1) {
                            e1.addSuppressed(e1);
                        }
                    }
                    throw e;
                }
                if (lines != null) {
                    lines.close();
                }
            } catch (Throwable ex) {
                try {
                    reader.close();
                } catch (Throwable e1) {
                    ex.addSuppressed(e1);
                }
                throw ex;
            }
            reader.close();
            PATTERNS = Collections.unmodifiableMap(patterns);
        } catch (IOException ioException) {
            IOException e = ioException;
            throw new UncheckedIOException(e);
        }
    }
}
