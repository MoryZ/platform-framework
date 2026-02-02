package com.old.silence.validation;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CollectionValidator implements SmartValidator {
    private final Validator validator;

    public CollectionValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        this.validate(target, errors, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        Collection<?> collection = (Collection)target;
        Iterator<?> iterator = collection.iterator();

        while(iterator.hasNext()) {
            Object object = iterator.next();
            ValidationUtils.invokeValidator(this.validator, object, errors, validationHints);
        }

    }
}
