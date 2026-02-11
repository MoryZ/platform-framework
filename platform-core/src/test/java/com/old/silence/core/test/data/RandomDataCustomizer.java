package com.old.silence.core.test.data;

/**
 * @author moryzang
 */
@FunctionalInterface
public interface RandomDataCustomizer<T> {

    void customize(T entity);
}
