package com.old.silence.core.context.distributed;

/**
 * @author moryzang
 */
public class DistributedContextImpl implements DistributedContext {

    private static final long serialVersionUID = -2817766011241763929L;

    private DistributedEvent distributedEvent;

    public DistributedContextImpl() {
    }

    public DistributedContextImpl(DistributedEvent distributedEvent) {
        this.distributedEvent = distributedEvent;
    }

    @Override
    public DistributedEvent getDistributedEvent() {
        return this.distributedEvent;
    }

    @Override
    public void setDistributedEvent(DistributedEvent distributedEvent) {
        this.distributedEvent = distributedEvent;
    }
}
