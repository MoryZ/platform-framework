package com.old.silence.autoconfigure.jdbc;

import java.lang.reflect.Constructor;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * @author MurrayZhang
 */
public final class DynamicDataSourceIdentifierHolder {

    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";

    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";

    public static final String MODE_GLOBAL = "MODE_GLOBAL";

    public static final String MODE_PRE_INITIALIZED = "MODE_PRE_INITIALIZED";

    public static final String SYSTEM_PROPERTY = "silence.datasource.strategy";

    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);

    private static DynamicDataSourceIdentifierHolderStrategy strategy;

    private static int initializeCount = 0;

    static {
        initialize();
    }

    private static void initialize() {
        initializeStrategy();
        initializeCount++;
    }

    private static void initializeStrategy() {

        if (MODE_PRE_INITIALIZED.equals(strategyName)) {
            Objects.requireNonNull(strategy, "When using " + MODE_PRE_INITIALIZED
                    + ", setIdentifierHolderStrategy must be called with the fully constructed strategy");
            return;
        }

        if (StringUtils.isBlank(strategyName)) {
            // Set default
            strategyName = MODE_THREADLOCAL;
        }

        switch (strategyName) {
            case MODE_THREADLOCAL:
                strategy = new ThreadLocalDynamicDataSourceIdentifierHolderStrategy();
                return;
            case MODE_INHERITABLETHREADLOCAL:
                strategy = new inheritableThreadLocalDynamicDataSourceIdentifierHolderStrategy();
                return;
            case MODE_GLOBAL:
                strategy = new GlobalDynamicDataSourceIdentifierHolderStrategy();
                return;
        }

        // try to load a custom strategy
        try {
            Class<?> clazz = Class.forName(strategyName);
            Constructor<?> customStrategy = clazz.getConstructor();
            strategy = (DynamicDataSourceIdentifierHolderStrategy) customStrategy.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearIdentifier() {
        strategy.clearIdentifier();
    }

    public static String getIdentifier() {
        return strategy.getIdentifier();
    }

    public static int getInitializeCount() {
        return initializeCount;
    }

    public static void setIdentifier(String identifier) {
        strategy.setIdentifier(identifier);
    }

    public static void setStrategyName(String strategyName) {
        DynamicDataSourceIdentifierHolder.strategyName = strategyName;
        initialize();
    }

    public static DynamicDataSourceIdentifierHolderStrategy getIdentifierHolderStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return "DynamicDataSourceIdentifierHolder[strategy='" + strategy.getClass().getSimpleName() + "'; initializeCount="
                + initializeCount + "]";
    }

}
