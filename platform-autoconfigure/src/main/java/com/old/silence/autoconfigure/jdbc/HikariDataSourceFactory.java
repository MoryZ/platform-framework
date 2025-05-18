package com.old.silence.autoconfigure.jdbc;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author MurrayZhang
 */
class HikariDataSourceFactory implements PooledDataSourceFactory<HikariDataSource> {
    @Override
    public String getExtensionName() {
        return "hikari";
    }

    @Override
    public HikariDataSource createDateSource(DataSourceProperties properties) throws SQLException {
        HikariDataSource dataSource = PooledDataSourceFactory.super.createDateSource(properties);
        if (StringUtils.isNotBlank(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }
}
