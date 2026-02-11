package com.old.silence.core.context.distributed;

import java.util.Objects;

/**
 * @author moryzang
 */
public class ThreadLocalDistributedContextHolderStrategy implements DistributedContextHolderStrategy {
    private static final ThreadLocal<DistributedContext> contextHolder = ThreadLocal.withInitial(DistributedContextImpl::new);

    public ThreadLocalDistributedContextHolderStrategy() {
    }

    @Override
    public void cleanContext() {
        contextHolder.remove();
    }

    @Override
    public DistributedContext getContext() {
        return contextHolder.get();
    }

    @Override
    public void setContext(DistributedContext context) {
        Objects.requireNonNull(context, "Only non-null DistributedContext instances are permitted");
        contextHolder.set(context);
    }

    @Override
    public DistributedContext createEmptyContext() {
        return new DistributedContextImpl();
    }
}
