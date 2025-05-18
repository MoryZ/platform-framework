package com.old.silence.json;

import java.util.TimeZone;

import org.springframework.core.KotlinDetector;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

public final class JacksonUtils {
    private static final Jackson2ObjectMapperBuilder BUILDER = customizeWithDefaultBuilder(Jackson2ObjectMapperBuilder.json());
    private static final StdTypeResolverBuilder TYPE_BUILDER = createDefaultTypeResolverBuilder();

    private JacksonUtils() {
        throw new AssertionError();
    }

    private static StdTypeResolverBuilder createDefaultTypeResolverBuilder() {
        StdTypeResolverBuilder typer = new TypeResolverBuilder(ObjectMapper.DefaultTyping.EVERYTHING, LaissezFaireSubTypeValidator.instance);
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer.inclusion(JsonTypeInfo.As.PROPERTY);
        return typer;
    }

    public static Jackson2ObjectMapperBuilder getDefaultJackson2ObjectMapperBuilder() {
        return BUILDER;
    }

    public static StdTypeResolverBuilder getDefaultTypeResolverBuilder() {
        return TYPE_BUILDER;
    }

    public static Jackson2ObjectMapperBuilder customizeWithDefaultBuilder(Jackson2ObjectMapperBuilder builder) {
        return builder.serializationInclusion(JsonInclude.Include.NON_EMPTY).failOnUnknownProperties(false).timeZone(TimeZone.getDefault()).featuresToEnable(MapperFeature.PROPAGATE_TRANSIENT_MARKER).featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT).featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT).featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T extends ObjectMapper> T configureWithDefaultBuilder(T mapper) {
        BUILDER.configure(mapper);
        return mapper;
    }

    private static JavaType resolveArrayOrWrapper(JavaType type) {
        while (type.isArrayType()) {
            type = type.getContentType();
            if (type.isReferenceType()) {
                type = resolveArrayOrWrapper(type);
            }
        }
        while (type.isReferenceType()) {
            type = type.getReferencedType();
            if (type.isArrayType()) {
                type = resolveArrayOrWrapper(type);
            }
        }
        return type;
    }

    private static class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {
        private static final long serialVersionUID = 8158618692361254959L;

        public TypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
            super(t, ptv);
        }

        @Override
        public ObjectMapper.DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
            return this;
        }

        /**
         * Method called to check if the default type handler should be used for given type. Note: "natural types" (String, * Boolean, Integer, Double) will never use typing; that is both due to them being concrete and final, and since * actual serializers and deserializers will also ignore any attempts to enforce typing.
         */
        @Override
        public boolean useForType(JavaType t) {
            if (t.isJavaLangObject()) {
                return true;
            }
            t = resolveArrayOrWrapper(t);
            if (t.isEnumType() || ClassUtils.isPrimitiveOrWrapper(t.getRawClass())) {
                return false;
            }
            if (t.isFinal() && !KotlinDetector.isKotlinType(t.getRawClass()) && t.getRawClass().getPackage().getName().startsWith("java")) {
                return false;
            }
            // [databind#88] Should not apply to JSON tree models:
            return !TreeNode.class.isAssignableFrom(t.getRawClass());
        }
    }
}
