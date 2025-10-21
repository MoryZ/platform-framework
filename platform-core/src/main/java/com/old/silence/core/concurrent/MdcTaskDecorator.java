package com.old.silence.core.concurrent;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

/**
 * @author moryzang
 */
public class MdcTaskDecorator implements TaskDecorator {

    public MdcTaskDecorator() {
    }

    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
        return new MdcDecoratedRunnable(runnable, MDC.getCopyOfContextMap());
    }

    private static class MdcDecoratedRunnable implements Runnable {
        private final Runnable delegate;
        private final Map<String, String> contextMap;
        public MdcDecoratedRunnable(Runnable delegate, Map<String, String> contextMap) {
            this.delegate = delegate;
            this.contextMap = contextMap;
        }
        @Override
        public void run() {
            MDC.setContextMap(contextMap);

            try {
                delegate.run();
            } finally {
                MDC.clear();
            }
        }
    }
}
