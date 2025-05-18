package com.old.silence.autoconfigure.jdbc;

import java.util.Objects;

/**
 * @author murrayZhang
 */
class ThreadLocalDynamicDataSourceIdentifierHolderStrategy implements DynamicDataSourceIdentifierHolderStrategy {

    private static final ThreadLocal<String> identifierHolder = new InheritableThreadLocal<>();

    @Override
    public void clearIdentifier() {
        identifierHolder.remove();
    }

    @Override
    public String getIdentifier() {
        return identifierHolder.get();
    }

    @Override
    public void setIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "Only non-null DataSource identifiers are permitted");
        identifierHolder.set(identifier);
    }
}
