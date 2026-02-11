package com.old.silence.core.logging;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * @author moryzang
 */
class MaskPatternFactoryTest {
    private static final List<String> PATTERN_NAMES = Arrays.asList("phone", "email", "id_card", "bank_card");

    @Test
    void testGetPatterns() {
        assertThat(MaskPatternFactory.getPatterns(PATTERN_NAMES)).hasSameSizeAs(PATTERN_NAMES);
    }
}
