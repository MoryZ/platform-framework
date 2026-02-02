package com.old.silence.autoconfigure.jdbc;

import oracle.ucp.jdbc.PoolDataSourceImpl;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * @author MurrayZhang
 */
class OracleDataSourceFactory implements PooledDataSourceFactory<PoolDataSourceImpl> {
    @Override
    public String getExtensionName() {
        return "oracleucp";
    }

    @Override
    public PoolDataSourceImpl createDateSource(DataSourceProperties properties) throws SQLException {
        PoolDataSourceImpl dataSource = PooledDataSourceFactory.super.createDateSource(properties);
        dataSource.setValidateConnectionOnBorrow(true);
        if (StringUtils.isNotBlank(properties.getName())) {
            dataSource.setConnectionPoolName(properties.getName());
        }
        return dataSource;
    }
}
