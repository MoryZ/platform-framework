package com.old.silence.core.concurrent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author moryzang
 */
public class DelegatingCompletableFuture<T> extends CompletableFuture<T> {
    private final Future<T> delegate;

    public DelegatingCompletableFuture(Future<T> delegate) {
        Objects.requireNonNull(delegate, "delegate must not be  null");
        this.delegate = delegate;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result = delegate.cancel(mayInterruptIfRunning);
        super.cancel(mayInterruptIfRunning);
        return result;
    }
}
