package com.old.silence.autoconfigure.jdbc;

import java.util.Map;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author moryzang
 */
@ConfigurationProperties(prefix = "spring.datasource")
public class RoutingDataSourceProperties {

    private Map<String, DataSourceProperties> routings;

    public Map<String, DataSourceProperties> getRoutings() {
        return routings;
    }

    public void setRoutings(Map<String, DataSourceProperties> routings) {
        this.routings = routings;
    }
}
