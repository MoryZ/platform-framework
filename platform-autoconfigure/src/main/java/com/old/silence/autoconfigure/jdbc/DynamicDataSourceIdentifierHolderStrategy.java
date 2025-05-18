package com.old.silence.autoconfigure.jdbc;

/**
 * @author moryzang
 */
public interface DynamicDataSourceIdentifierHolderStrategy {

    void clearIdentifier();

    String getIdentifier();

    void setIdentifier(String identifier);
}
