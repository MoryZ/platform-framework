package com.old.silence.webmvc.exception;

import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.validation.BindException;
import com.old.silence.core.exception.PlatformException;

public class MvcExceptionHandlerFactory {

    private static final Map<Class<? extends Throwable>, Function<MessageSourceAccessor, MvcExceptionHandler>> DEFAULT_HANDLER_FACTORIES;

    private final Map<Class<? extends Throwable>, MvcExceptionHandler> mappedHandlers;

    private final Map<Class<? extends Throwable>, MvcExceptionHandler> exceptionLookupCache = new ConcurrentReferenceHashMap<>(
            16);

    static {
        Map<Class<? extends Throwable>, Function<MessageSourceAccessor, MvcExceptionHandler>> defaultHandlerFactories = new HashMap<>();
        defaultHandlerFactories.put(BindException.class, BindExceptionHandler::new);
        defaultHandlerFactories.put(ConstraintViolationException.class, ConstraintViolationExceptionHandler::new);
        defaultHandlerFactories.put(PlatformException.class, PlatformExceptionHandler::new);
        defaultHandlerFactories.put(Throwable.class, ThrowableHandler::new);

        DEFAULT_HANDLER_FACTORIES = Collections.unmodifiableMap(defaultHandlerFactories);
    }
    public MvcExceptionHandlerFactory(MessageSourceAccessor messageSourceAccessor,
                                      ObjectProvider<MvcExceptionHandler> exceptionHandlers) {

        this.mappedHandlers = collectPotentialMappedHandlers(messageSourceAccessor, exceptionHandlers);
    }

    private static Map<Class<? extends Throwable>, MvcExceptionHandler> collectPotentialMappedHandlers(
            MessageSourceAccessor messageSourceAccessor, ObjectProvider<MvcExceptionHandler> exceptionHandlers) {

        Map<Class<? extends Throwable>, MvcExceptionHandler> mappedHandlers = exceptionHandlers.orderedStream()
                .collect(Collectors.toMap(MvcExceptionHandler::getSupportedExceptionType, Function.identity()));

        DEFAULT_HANDLER_FACTORIES.entrySet().stream().filter(entry -> !mappedHandlers.containsKey(entry.getKey()))
                .forEach(entry -> mappedHandlers.put(entry.getKey(), entry.getValue().apply(messageSourceAccessor)));

        return Collections.unmodifiableMap(mappedHandlers);
    }
    public MvcExceptionHandler getExceptionHandler(Class<? extends Throwable> exceptionType) {
        return exceptionLookupCache.computeIfAbsent(exceptionType, type -> {
            List<Class<? extends Throwable>> matches = new ArrayList<>();
            for (Class<? extends Throwable> mappedException : mappedHandlers.keySet()) {
                if (mappedException.isAssignableFrom(type)) {
                    matches.add(mappedException);
                }
            }
            if (matches.isEmpty()) {
                throw new IllegalStateException("Can not find MvcExceptionHandler of exception type [" + type + ']');
            } else {
                if (matches.size() > 1) {
                    matches.sort(new ExceptionDepthComparator(type));
                }
                return mappedHandlers.get(matches.get(0));
            }
        });
    }

}
