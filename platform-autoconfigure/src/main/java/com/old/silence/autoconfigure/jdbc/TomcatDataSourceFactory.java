package com.old.silence.autoconfigure.jdbc;


import java.sql.SQLException;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;

/**
 * @author MurrayZhang
 */
class TomcatDataSourceFactory implements PooledDataSourceFactory<org.apache.tomcat.jdbc.pool.DataSource> {
    @Override
    public String getExtensionName() {
        return "tomcat";
    }

    @Override
    public org.apache.tomcat.jdbc.pool.DataSource createDateSource(DataSourceProperties properties) throws SQLException {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = PooledDataSourceFactory.super.createDateSource(properties);
        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
        var validationQuery = databaseDriver.getValidationQuery();
        if (validationQuery != null) {
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(validationQuery);
        }
        return dataSource;
    }
}
