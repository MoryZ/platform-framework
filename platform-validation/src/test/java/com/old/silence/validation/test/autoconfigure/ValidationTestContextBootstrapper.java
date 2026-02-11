package com.old.silence.validation.test.autoconfigure;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.core.annotation.MergedAnnotations;

/**
 * @author moryzang
 */
public class ValidationTestContextBootstrapper extends SpringBootTestContextBootstrapper {

    public ValidationTestContextBootstrapper() {
    }

    protected String[] getProperties(Class<?> testClass) {
        return MergedAnnotations.from(testClass, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS).get(ValidationTest.class)
                .getValue("properties", String[].class).orElse(null);
    }
}
