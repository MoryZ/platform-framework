package com.old.silence.core.context.distributed;

import java.io.Serializable;

/**
 * @author moryzang
 */
public interface DistributedContext extends Serializable {

    DistributedEvent getDistributedEvent();

    void setDistributedEvent(DistributedEvent distributedEvent);
}
