package com.old.silence.autoconfigure.condition;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author murrayZhang
 */
public class MacWithSiliconChip extends SpringBootCondition {

    private static final String SILICON_ARCH = "aarch64";
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (SystemUtils.IS_OS_MAC && SILICON_ARCH.equals(SystemUtils.OS_ARCH)) {
            return ConditionOutcome.match("Mac with silicon chip");
        } else {
            return ConditionOutcome.match("Not Mac with silicon chip");
        }
    }
}
