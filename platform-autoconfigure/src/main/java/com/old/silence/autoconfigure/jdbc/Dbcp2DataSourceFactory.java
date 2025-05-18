package com.old.silence.autoconfigure.jdbc;


import org.apache.commons.dbcp2.BasicDataSource;

/**
 * @author MurrayZhang
 */
class Dbcp2DataSourceFactory implements PooledDataSourceFactory<BasicDataSource> {
    @Override
    public String getExtensionName() {
        return "dbcp";
    }

}
