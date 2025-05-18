package com.old.silence.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JacksonMapper {
    private static volatile JacksonMapper sharedInstance;
    private final ObjectMapper mapper;
    private final ConcurrentMap<Class<?>, ObjectReader> objectReaderCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<JavaType, ObjectReader> javaTypeObjectReaderCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, ObjectWriter> objectWriterCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<JavaType, ObjectWriter> javaTypeObjectWriterCache = new ConcurrentHashMap<>();

    public JacksonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static JacksonMapper getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (JacksonMapper.class) {
                if (sharedInstance == null) {
                    ObjectMapper objectMapper = JacksonUtils.getDefaultJackson2ObjectMapperBuilder().build();
                    sharedInstance = new JacksonMapper(objectMapper);
                }
            }
        }
        return sharedInstance;
    }

    public String toJson(Object object) {
        if (object == null) {
            return null;
        }

        try {
            ObjectWriter writer = getObjectWriter(mapper, object.getClass());
            return writer.writeValueAsString(object);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] toJsonAsBytes(Object object) {
        if (object == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        try {
            ObjectWriter writer = getObjectWriter(mapper, object.getClass());
            return writer.writeValueAsBytes(object);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeJson(OutputStream out, Object value) {
        if (value == null) {
            return;
        }
        try {
            ObjectWriter writer = getObjectWriter(mapper, value.getClass());
            writer.writeValue(out, value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(String jsonString, Class<T> type) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            ObjectReader reader = getObjectReader(mapper, type);
            return reader.readValue(jsonString);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(byte[] src, Class<T> type) {
        if (ArrayUtils.isEmpty(src)) {
            return null;
        }

        try {
            ObjectReader reader = getObjectReader(mapper, type);
            return reader.readValue(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(InputStream src, Class<T> type) {
        try {
            if (src == null || src.available() == 0) {
                return null;
            }
            ObjectReader reader = getObjectReader(mapper, type);
            return reader.readValue(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(JsonNode src, Class<T> type) {
        if (src == null || src.isEmpty()) {
            return null;
        }

        try {
            ObjectReader reader = getObjectReader(mapper, type);
            return reader.readValue(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            ObjectReader reader = getObjectReader(mapper, javaType);
            return reader.readValue(jsonString);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(byte[] src, JavaType javaType) {
        if (ArrayUtils.isEmpty(src)) {
            return null;
        }

        try {
            ObjectReader reader = getObjectReader(mapper, javaType);
            return reader.readValue(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(InputStream src, JavaType javaType) {
        try {
            if (src == null || src.available() == 0) {
                return null;
            }

            ObjectReader reader = getObjectReader(mapper, javaType);
            return reader.readValue(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T fromJson(JsonNode src, JavaType javaType) {
        if (src == null || src.isEmpty()) {
            return null;
        }

        try {
            ObjectReader reader = getObjectReader(mapper, javaType);
            return reader.readValue(src);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ObjectReader getObjectReader(ObjectMapper mapper, Class<?> type) {
        return objectReaderCache.computeIfAbsent(type, mapper::readerFor);
    }

    public ObjectReader getObjectReader(ObjectMapper mapper, JavaType type) {
        return javaTypeObjectReaderCache.computeIfAbsent(type, mapper::readerFor);
    }

    public ObjectWriter getObjectWriter(ObjectMapper mapper, Class<?> type) {
        return objectWriterCache.computeIfAbsent(type, mapper::writerFor);
    }

    public ObjectWriter getObjectWriter(ObjectMapper mapper, JavaType type) {
        return javaTypeObjectWriterCache.computeIfAbsent(type, mapper::writerFor);
    }

    public <T> List<T> fromCollectionJson(byte[] src, Class<T> clazz) {
        return fromJson(src, constructListType(clazz));
    }

    public <T> List<T> fromCollectionJson(String jsonString, Class<T> clazz) {
        return fromJson(jsonString, constructListType(clazz));
    }

    public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    public JavaType constructArrayType(Class<?> elementClass) {
        return mapper.getTypeFactory().constructArrayType(elementClass);
    }

    public JavaType constructListType(Class<?> elementClass) {
        return constructCollectionType(List.class, elementClass);
    }

    @SuppressWarnings("rawtypes")
    public JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public JavaType constructMapType(Class<?> keyClass, Class<?> valueClass) {
        return constructMapType(HashMap.class, keyClass, valueClass);
    }

    @SuppressWarnings("rawtypes")
    public JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public boolean validateJson(String json) {
        try {
            try (JsonParser parser = mapper.getFactory().createParser(json)) {
                while (parser.nextToken() != null) {
                    // left blank intentionally
                }
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public ObjectMapper unwrap() {
        return mapper;
    }
}
