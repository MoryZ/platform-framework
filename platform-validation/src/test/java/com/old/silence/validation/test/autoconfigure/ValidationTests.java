package com.old.silence.validation.test.autoconfigure;

import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.ArrayUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import com.old.silence.core.test.UnitTests;
import com.old.silence.core.test.data.RandomData;
import com.old.silence.validation.constraint.Id;
import com.old.silence.validation.group.CreationValidation;
import com.old.silence.validation.group.UpdateValidation;

/**
 * @author moryzang
 */
@ValidationTest
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class ValidationTests extends UnitTests {

    @Autowired
    private Validator validator;

    protected <T> void assertNullIdViolation(Class<T> type, String propertyName, BiConsumer<T, Long> idPropertySetter,
                                             Class<?>... groups) {
        T command = BeanUtils.instantiateClass(type);
        expectEmptyViolation(command, propertyName, groups);

        idPropertySetter.accept(command, RandomData.randomId());
        expectConstraintAnnotationType(command, propertyName, Null.class, groups);
    }


    protected <T> void expectEmptyViolation(T targetBean, String propertyName, Class<?>... groups) {
        var constraintViolations = validator.validateProperty(targetBean, propertyName, getDefaultValidationGroups(groups));
        assertThat(constraintViolations).isEmpty();
    }

    protected <T> void expectConstraintAnnotationType(T targetBean, String propertyName, Class<?> annotationType,
                                                      Class<?>... groups) {
        var constraintViolations = validator.validateProperty(targetBean, propertyName,
                getDefaultValidationGroups(groups));
        assertThat(constraintViolations).hasSize(1);

        var violation = constraintViolations.iterator().next();
        assertThat(violation.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(annotationType);
    }

    protected static Class<?>[] getDefaultValidationGroups(Class<?>... groups) {
        return ArrayUtils.isEmpty(groups) ? new Class[]{
            CreationValidation.class, UpdateValidation.class
        } : groups;
    }

    protected <T> void assertIdViolation(Class<T> type, String propertyName, BiConsumer<T, Long> idPropertySetter,
                                         Class<?>... groups) {
        var command = BeanUtils.instantiateClass(type);
        expectConstraintAnnotationType(command, propertyName, Id.class, groups);

        idPropertySetter.accept(command, -RandomData.randomId());
        expectConstraintAnnotationType(command, propertyName, Id.class, groups);

        idPropertySetter.accept(command, RandomData.randomId());
        expectEmptyViolation(command, propertyName, groups);

    }

    protected <T> void assertStringFieldsViolation(Class<T> type, String propertyName,
                                                   BiConsumer<T, String> propertySetter, Class<?>... groups) {
        assertStringFieldViolation(type, propertyName, propertySetter, true, groups);
    }

    protected <T> void assertStringFieldViolation(Class<T> type, String propertyName,
                                                  BiConsumer<T, String> propertySetter, boolean isNotBlank, Class<?>... groups) {
        var command = BeanUtils.instantiateClass(type);
        if (isNotBlank) {
            propertySetter.accept(command, "  ");
            expectConstraintAnnotationType(command, propertyName, NotBlank.class, groups);
        }

        Field property = ReflectionUtils.findField(command.getClass(), propertyName);
        Objects.requireNonNull(property);
        var size = AnnotationUtils.findAnnotation(property, Size.class);
        assertThat(size).isNotNull();
        propertySetter.accept(command, RandomData.randomName(propertyName, size.max() + 1));
        expectConstraintAnnotationType(command, propertyName, Size.class, groups);

        propertySetter.accept(command, RandomData.randomName(propertyName, size.max()));
        expectEmptyViolation(command, propertyName);

    }

    protected <T, E extends Enum<E>> void assertEnumFieldViolation(Class<T> type, String propertyName,
                                                                  Class<E> enumType, BiConsumer<T, E> propertySetter, Class<?>... groups) {
        var command = BeanUtils.instantiateClass(type);
        expectConstraintAnnotationType(command, propertyName, NotNull.class, groups);

        propertySetter.accept(command, RandomData.randomEnum(enumType));
        expectEmptyViolation(command, propertyName, groups);

    }
}
