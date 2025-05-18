package com.old.silence.web.autoconfigure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.converter.HttpMessageConverter;

class CustomHttpMessageConverters extends HttpMessageConverters {
    public CustomHttpMessageConverters(boolean addDefaultConverters, Collection<HttpMessageConverter<?>> converters) {
        super(addDefaultConverters, converters);
    }

    public CustomHttpMessageConverters(Collection<HttpMessageConverter<?>> additionalConverters) {
        super(additionalConverters);
    }

    public CustomHttpMessageConverters(HttpMessageConverter<?>... additionalConverters) {
        super(additionalConverters);
    }

    protected List<HttpMessageConverter<?>> postProcessConverters(List<HttpMessageConverter<?>> converters) {
        Set<Class<?>> converterTypes = new HashSet();
        Iterator<HttpMessageConverter<?>> iterator = converters.iterator();

        while(iterator.hasNext()) {
            HttpMessageConverter<?> candidate = (HttpMessageConverter)iterator.next();
            if (!converterTypes.add(candidate.getClass())) {
                iterator.remove();
            }
        }

        return converters;
    }
}
