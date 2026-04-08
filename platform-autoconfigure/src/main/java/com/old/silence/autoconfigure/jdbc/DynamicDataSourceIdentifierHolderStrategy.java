package com.old.silence.autoconfigure.jdbc;

/**
 * @author moryZhang
 */
public interface DynamicDataSourceIdentifierHolderStrategy {

    void clearIdentifier();

    String getIdentifier();

    void setIdentifier(String identifier);
}
