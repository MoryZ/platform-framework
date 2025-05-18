package com.old.silence.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.StreamUtils;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.old.silence.core.exception.PlatformException;
import com.old.silence.core.support.ValidationErrorCode;
import com.old.silence.core.util.CollectionUtils;
import com.old.silence.http.converter.support.CsvFilenameGenerator;
import com.old.silence.http.converter.support.DefaultCsvFilenameGenerator;

public class MappingJackson2CsvHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public static final String TEXT_CSV_VALUE = "text/csv";
    private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMapWithExpectedSize(JsonEncoding.values().length);
    private final CsvMapper csvMapper;
    private final CsvFilenameGenerator csvFilenameGenerator;
    private final ConcurrentMap<CacheKey, ObjectReader> objectReaderCache = new ConcurrentHashMap();
    private final ConcurrentMap<CacheKey, ObjectWriter> objectWriterCache = new ConcurrentHashMap();
    private boolean includeHeader = true;

    public MappingJackson2CsvHttpMessageConverter(CsvMapper csvMapper, Optional<CsvFilenameGenerator> csvFilenameGeneratorProvider) {
        super(csvMapper, MediaType.parseMediaType("text/csv"));
        this.csvMapper = csvMapper;
        this.csvFilenameGenerator = (CsvFilenameGenerator)csvFilenameGeneratorProvider.orElseGet(DefaultCsvFilenameGenerator::new);
        this.setDefaultCharset(Charset.forName("GBK"));
    }

    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        super.setDefaultCharset((Charset) Objects.requireNonNull(defaultCharset));
    }

    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(type, contextClass);
        return this.readJavaTypeInternal(javaType, inputMessage);
    }

    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(clazz, (Class)null);
        return this.readJavaTypeInternal(javaType, inputMessage);
    }

    private Object readJavaTypeInternal(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = this.getCharset(contentType);
        boolean isUnicode = ENCODINGS.containsKey(charset.name()) || "UTF-16".equals(charset.name()) || "UTF-32".equals(charset.name());

        try {
            Class<?> schemaType = this.getSchemaType(javaType);
            Class<?> deserializationView = getViewType(inputMessage);
            ObjectReader objectReader = (ObjectReader)this.objectReaderCache.computeIfAbsent(CacheKey.of(schemaType, deserializationView), (key) -> {
                return this.csvMapper.readerFor(key.type).with(this.createSchema(key));
            });
            InputStream inputStream = StreamUtils.nonClosing(inputMessage.getBody());
            MappingIterator<Object> iterator = this.readValues(charset, isUnicode, inputStream, objectReader);
            List var11;
            try {
                var11 = iterator.readAll();
            } catch (Throwable var14) {
                if (iterator != null) {
                    try {
                        iterator.close();
                    } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                    }
                }

                throw var14;
            }

            if (iterator != null) {
                iterator.close();
            }

            return var11;
        } catch (InvalidDefinitionException var15) {
            throw new HttpMessageConversionException("Type definition error: " + var15.getType(), var15);
        } catch (CsvReadException var16) {
            throw new PlatformException(ValidationErrorCode.INVALID_PARAMETER, var16.getOriginalMessage() + ": line " + var16.getLocation().getLineNr(), var16);
        } catch (JsonProcessingException var17) {
            throw new HttpMessageNotReadableException("CSV parse error: " + var17.getOriginalMessage(), var17, inputMessage);
        }
    }

    private MappingIterator<Object> readValues(Charset charset, boolean isUnicode, InputStream inputStream, ObjectReader objectReader) throws IOException {
        if (isUnicode) {
            return objectReader.readValues(inputStream);
        } else {
            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            return objectReader.readValues(reader);
        }
    }

    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        Class<?> schemaType = this.getSchemaType(type);
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(this.csvFilenameGenerator.generate(schemaType), StandardCharsets.UTF_8).build();
        headers.setContentDisposition(contentDisposition);
        OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
        Charset charset = (Charset)Optional.ofNullable(headers.getContentType()).map(MimeType::getCharset).orElseGet(this::getDefaultCharset);

        try {
            CsvGenerator generator = this.csvMapper.getFactory().createGenerator(new OutputStreamWriter(outputStream, charset));

            try {
                Object value = object;
                Class<?> serializationView = null;
                FilterProvider filters = null;
                if (object instanceof MappingJacksonValue) {
                    MappingJacksonValue container = (MappingJacksonValue)object;
                    value = container.getValue();
                    serializationView = container.getSerializationView();
                    filters = container.getFilters();
                }

                ObjectWriter objectWriter = (ObjectWriter)this.objectWriterCache.computeIfAbsent(CacheKey.of(schemaType, serializationView), (key) -> {
                    return this.csvMapper.writer(this.createSchema(key));
                });
                if (filters != null) {
                    objectWriter = objectWriter.with(filters);
                }

                objectWriter.writeValue(generator, value);
                generator.flush();
            } catch (Throwable var15) {
                if (generator != null) {
                    try {
                        generator.close();
                    } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                    }
                }

                throw var15;
            }

            if (generator != null) {
                generator.close();
            }

        } catch (InvalidDefinitionException var16) {
            throw new HttpMessageConversionException("Type definition error: " + var16.getType(), var16);
        } catch (JsonProcessingException var17) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + var17.getOriginalMessage(), var17);
        }
    }
    protected Charset getCharset(@Nullable MediaType contentType) {
        return (Charset)Optional.ofNullable(contentType).map(MimeType::getCharset).orElse(this.getDefaultCharset());
    }

    private Class<?> getSchemaType(Type type) {
        return this.getSchemaType(this.getJavaType(type, (Class)null));
    }

    private Class<?> getSchemaType(JavaType javaType) {
        Class<?> rawType;
        if (javaType.isCollectionLikeType()) {
            rawType = (Class)Optional.ofNullable(javaType.getContentType()).map(JavaType::getRawClass).orElse(null);
            if (rawType == null) {
                return null;
            }
        } else {
            rawType = javaType.getRawClass();
        }

        Class<?> mixInType = this.csvMapper.findMixInClassFor(rawType);
        return mixInType == null ? rawType : mixInType;
    }

    private CsvSchema createSchema(CacheKey key) {
        CsvSchema schema;
        if (key.view == null) {
            schema = this.csvMapper.schemaFor(key.type);
        } else {
            schema = this.csvMapper.schemaForWithView(key.type, key.view);
        }

        if (this.includeHeader) {
            schema = schema.withHeader();
        }

        return schema;
    }
    private static Class<?> getViewType(HttpInputMessage inputMessage) {
        Class<?> deserializationView = null;
        if (inputMessage instanceof MappingJacksonInputMessage) {
            deserializationView = ((MappingJacksonInputMessage)inputMessage).getDeserializationView();
        }

        return deserializationView;
    }

    static {
        JsonEncoding[] var0 = JsonEncoding.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            JsonEncoding encoding = var0[var2];
            ENCODINGS.put(encoding.getJavaName(), encoding);
        }

        ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
    }

    private static class CacheKey {
        private final Class<?> type;
        private final Class<?> view;

        private CacheKey(Class<?> type, Class<?> view) {
            this.type = type;
            this.view = view;
        }
        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.view});
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (this.getClass() != obj.getClass()) {
                return false;
            } else {
                CacheKey other = (CacheKey)obj;
                return Objects.equals(this.type, other.type) && Objects.equals(this.view, other.view);
            }
        }

        public static CacheKey of(Class<?> type, Class<?> view) {
            return new CacheKey(type, view);
        }
    }

}
