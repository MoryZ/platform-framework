package com.old.silence.core.context.distributed;

/**
 * @author moryzang
 */
public class DistributedContextHolder {
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    private static int initializeCount;
    private static DistributedContextHolderStrategy strategy;

    public DistributedContextHolder() {
    }

    private static void initialize() {
        initializeStrategy();
        ++initializeCount;
    }

    private static void initializeStrategy() {
        strategy = new ThreadLocalDistributedContextHolderStrategy();
    }

    public static void clearContext() {
        strategy.cleanContext();
    }

    public static DistributedContext getContext() {
        return strategy.getContext();
    }

    public static void setContext(DistributedContext context) {
        strategy.setContext(context);
    }

    public static DistributedContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    public static DistributedContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    public String toString() {
        return "DistributedContextHolder[strategy='" + strategy.getClass().getSimpleName() + "'; initializeCount=" + initializeCount + "]";
    }

    static {
        initialize();
    }
}
