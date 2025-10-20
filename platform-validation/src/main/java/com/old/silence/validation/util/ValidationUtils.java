package com.old.silence.validation.util;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.ConstraintDescriptor;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import com.old.silence.core.context.MessageSourceAccessorHolder;
import com.old.silence.core.support.ErrorCoded;
import com.old.silence.core.support.ValidationErrorCode;

public final class ValidationUtils {
    private static final String ERROR_CODE_ATTRIBUTE = "errorCode";
    private static final ErrorCoded DEFAULT_ERROR_CODE;
    private static final Map<Class<? extends Annotation>, ErrorCoded> DEFAULT_CONSTRAINT_ERROR_CODE_MAP;

    private ValidationUtils() {
        throw new AssertionError();
    }

    private static Map<Class<? extends Annotation>, ErrorCoded> createDefaultConstraintErrorCodeMap() {
        Map<Class<? extends Annotation>, ErrorCoded> errorCodeMap = new HashMap();
        errorCodeMap.put(NotNull.class, ValidationErrorCode.MISSING_REQUIRED_PARAMETER);
        errorCodeMap.put(NotEmpty.class, ValidationErrorCode.MISSING_REQUIRED_PARAMETER);
        errorCodeMap.put(NotBlank.class, ValidationErrorCode.MISSING_REQUIRED_PARAMETER);
        return Collections.unmodifiableMap(errorCodeMap);
    }

    public static ErrorCoded extractErrorCode(ConstraintViolation<?> violation) {
        return extractErrorCode(violation, (Map)null);
    }

    public static ErrorCoded extractErrorCode(ConstraintViolation<?> violation, Map<Class<? extends Annotation>, ErrorCoded> constraintErrorCodeMap) {
        ConstraintDescriptor<?> constraintDescriptor = violation.getConstraintDescriptor();
        ErrorCoded errorCode = (ErrorCoded)((Map) ObjectUtils.defaultIfNull(constraintErrorCodeMap, DEFAULT_CONSTRAINT_ERROR_CODE_MAP)).get(constraintDescriptor.getAnnotation().annotationType());
        if (errorCode != null) {
            return errorCode;
        } else {
            errorCode = DEFAULT_ERROR_CODE;
            Map<String, Object> attributes = constraintDescriptor.getAttributes();
            Object errorCodeAttribute = attributes.get(ERROR_CODE_ATTRIBUTE);
            if (errorCodeAttribute instanceof ErrorCoded) {
                errorCode = (ErrorCoded)errorCodeAttribute;
            }

            return errorCode;
        }
    }

    public static String getViolationMessage(ConstraintViolation<?> violation) {
        return getViolationMessage(violation, MessageSourceAccessorHolder.getAccessor());
    }
    public static String getViolationMessage(ConstraintViolation<?> violation, MessageSourceAccessor messageSourceAccessor) {
        String field = "";
        Path.Node leafNode = null;

        Path.Node node;
        for(Iterator var4 = violation.getPropertyPath().iterator(); var4.hasNext(); leafNode = node) {
            node = (Path.Node)var4.next();
        }

        if (leafNode != null) {
            String nodeName = leafNode.getName();

            try {
                field = messageSourceAccessor.getMessage(nodeName, nodeName);
            } catch (NoSuchMessageException var6) {
                field = nodeName;
            }
        }

        MessageFormat format = new MessageFormat(violation.getMessage(), LocaleContextHolder.getLocale());
        String message = format.format(new Object[]{field});
        return StringUtils.capitalize(message);
    }

    static {
        DEFAULT_ERROR_CODE = ValidationErrorCode.INVALID_PARAMETER;
        DEFAULT_CONSTRAINT_ERROR_CODE_MAP = createDefaultConstraintErrorCodeMap();
    }
}
