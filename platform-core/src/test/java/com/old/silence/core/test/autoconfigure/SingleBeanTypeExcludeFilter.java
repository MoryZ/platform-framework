package com.old.silence.core.test.autoconfigure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.test.autoconfigure.filter.AnnotationCustomizableTypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author moryzang
 */
class SingleBeanTypeExcludeFilter extends AnnotationCustomizableTypeExcludeFilter {

    private final SingleBeanTest annotation;

    SingleBeanTypeExcludeFilter(Class<?> testClass) {
        this.annotation = AnnotatedElementUtils.getMergedAnnotation(testClass, SingleBeanTest.class);
    }

    @Override
    protected boolean hasAnnotation() {
        return this.annotation != null;
    }

    @Override
    protected ComponentScan.Filter[] getFilters(FilterType type) {
        switch (type) {
            case INCLUDE ->
            {
                return this.annotation.includeFilters();
            }
            case EXCLUDE -> {
                return this.annotation.excludeFilters();
            }
            default -> throw new IllegalStateException("Unsupported type" + type);
        }
    }

    @Override
    protected boolean isUseDefaultFilters() {
        return this.annotation.useDefaultFilters();
    }

    @Override
    protected Set<Class<?>> getDefaultIncludes() {
        return Collections.emptySet();
    }

    @Override
    protected Set<Class<?>> getComponentIncludes() {
        Set<Class<?>> types = new HashSet<>();
        Collections.addAll(types, this.annotation.value());
        return types;
    }
}
