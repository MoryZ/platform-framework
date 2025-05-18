package com.old.silence.core.enums;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * @author murrayZhang
 */
public class ClassPathEnumValueScanner extends ClassPathScanningCandidateComponentProvider {

    public ClassPathEnumValueScanner() {
        super(false);
        addIncludeFilter(new AssignableTypeFilter(EnumValue.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }
}
