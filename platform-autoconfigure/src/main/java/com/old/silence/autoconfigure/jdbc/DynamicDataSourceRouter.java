package com.old.silence.autoconfigure.jdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author murrayZhang
 */
public class DynamicDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceIdentifierHolder.getIdentifier();
    }

}
