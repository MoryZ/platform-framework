package com.old.silence.webmvc.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

public class SpringDataWebAutoConfigurationExclusionFilter implements AutoConfigurationImportFilter {

    private static final String SPRING_DATA_WEB_AUTO_CONFIGURATION_CLASS = "org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration";

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {

        boolean[] matches = new boolean[autoConfigurationClasses.length];

        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            matches[i] = !SPRING_DATA_WEB_AUTO_CONFIGURATION_CLASS.equals(autoConfigurationClasses[i]);
        }

        return matches;
    }
}
