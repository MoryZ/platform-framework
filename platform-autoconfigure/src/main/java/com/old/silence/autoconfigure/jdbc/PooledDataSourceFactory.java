package com.old.silence.autoconfigure.jdbc;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.GenericTypeResolver;

/**
 * @author MurrayZhang
 */
interface PooledDataSourceFactory<T extends DataSource> {

    @SuppressWarnings("unchecked")
    default Class<T> getSupportedType() {
        return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), PooledDataSourceFactory.class);
    }

    String getExtensionName();

    default T createDateSource(DataSourceProperties properties) throws SQLException {
        return properties.initializeDataSourceBuilder().type(getSupportedType()).build();
    }
}
