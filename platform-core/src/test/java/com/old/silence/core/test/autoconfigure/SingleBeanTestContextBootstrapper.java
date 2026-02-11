package com.old.silence.core.test.autoconfigure;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.core.annotation.MergedAnnotations;

/**
 * @author moryzang
 */
class SingleBeanTestContextBootstrapper extends SpringBootTestContextBootstrapper {

    @Override
    protected String[] getProperties(Class<?> testClass) {
        return MergedAnnotations.from(testClass, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS).get(SingleBeanTest.class)
                .getValue("properties", String[].class).orElse(null);
    }
}
