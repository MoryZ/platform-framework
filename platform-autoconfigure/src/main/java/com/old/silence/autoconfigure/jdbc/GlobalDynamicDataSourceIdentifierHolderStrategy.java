package com.old.silence.autoconfigure.jdbc;

/**
 * @author moryzang
 */
class GlobalDynamicDataSourceIdentifierHolderStrategy implements DynamicDataSourceIdentifierHolderStrategy {

    private static String identifierHolder;

    @Override
    public void clearIdentifier() {
        identifierHolder = null; // NOSONAR
    }

    @Override
    public String getIdentifier() {
        return identifierHolder;
    }

    @Override
    public void setIdentifier(String identifier) {
        identifierHolder = identifier;
    }
}
