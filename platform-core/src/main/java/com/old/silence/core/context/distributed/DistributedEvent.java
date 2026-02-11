package com.old.silence.core.context.distributed;

import java.io.Serializable;

/**
 * @author moryzang
 */
@FunctionalInterface
public interface DistributedEvent extends Serializable {

    boolean validate();
}
