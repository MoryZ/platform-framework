package com.old.silence.core.concurrent;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author moryzang
 */
public class CallerBlocksPolicy implements RejectedExecutionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CallerBlocksPolicy.class);
    private final long maxWait;

    public CallerBlocksPolicy() {
        this(Duration.ZERO);
    }

    public CallerBlocksPolicy(Duration maxWait) {
        this.maxWait = maxWait.toMillis();
    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Executor has been shutdown");
        } else {
            try {
                BlockingQueue<Runnable> queue = executor.getQueue();
                logger.debug("Attempt to queue task execution for {} milliseconds", this.maxWait);
                if (this.maxWait <= 0L) {
                    queue.put(r);
                } else if (!queue.offer(r, this.maxWait, TimeUnit.MILLISECONDS)) {
                    throw new RejectedExecutionException("Max wait time expired to queue task");
                }

                logger.debug("Task execution queued");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Interrupted", e);
            }
        }
    }
}
