package com.old.silence.validation.test.autoconfigure;

import org.springframework.boot.test.autoconfigure.filter.StandardAnnotationCustomizableTypeExcludeFilter;

/**
 * @author moryzang
 */
class ValidationTypeExcludeFilter extends StandardAnnotationCustomizableTypeExcludeFilter<ValidationTest> {

    public ValidationTypeExcludeFilter(Class<?> testClass) {
        super(testClass);
    }
}
