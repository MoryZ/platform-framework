package com.old.silence.core.context.distributed;

/**
 * @author moryzang
 */
public interface DistributedContextHolderStrategy {

    void cleanContext();

    DistributedContext getContext();

    void setContext(DistributedContext context);

    DistributedContext createEmptyContext();
}
