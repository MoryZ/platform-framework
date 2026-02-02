package com.old.silence.autoconfigure.jdbc;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * @author MurrayZhang
 */
class GenericDataSourceFactory implements PooledDataSourceFactory<DataSource> {
    @Override
    public String getExtensionName() {
        return null;
    }

    @Override
    public DataSource createDateSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
