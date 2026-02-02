package com.old.silence.autoconfigure.jdbc;

/**
 * @author murrayZhang
 */
public interface DynamicDataSourceIdentifierHolderStrategy {

    void clearIdentifier();

    String getIdentifier();

    void setIdentifier(String identifier);
}
