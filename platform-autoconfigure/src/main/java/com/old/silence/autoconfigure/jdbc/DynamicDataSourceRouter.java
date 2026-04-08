package com.old.silence.autoconfigure.jdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author moryZhang
 */
public class DynamicDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceIdentifierHolder.getIdentifier();
    }

}
