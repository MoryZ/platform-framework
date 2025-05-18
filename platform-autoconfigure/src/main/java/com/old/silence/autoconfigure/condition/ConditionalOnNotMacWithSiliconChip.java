package com.old.silence.autoconfigure.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.context.annotation.Conditional;

/**
 * @author moryzang
 */
@Target({ ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({ConditionalOnNotMacWithSiliconChip.NotMacWithSiliconChipCondition.class})
public @interface ConditionalOnNotMacWithSiliconChip {

    static class NotMacWithSiliconChipCondition extends NoneNestedConditions {

        public NotMacWithSiliconChipCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnMacWithSiliconChip
        static class OnMacWithSiliconChip {

        }
    }
}
