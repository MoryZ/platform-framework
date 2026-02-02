package com.old.silence.validation.beanvalidation;


import jakarta.validation.ConstraintValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

public class EnhancedSpringConstraintValidatorFactory extends SpringConstraintValidatorFactory {
    private static final Logger logger = LoggerFactory.getLogger(EnhancedSpringConstraintValidatorFactory.class);
    private final AutowireCapableBeanFactory beanFactory;

    public EnhancedSpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
        super(beanFactory);
        this.beanFactory = beanFactory;
    }

    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        T bean = null;
        try {
            bean = super.getInstance(key);
        } catch (
                BeansException beansException) {
            logger.info("Failed to create validator bean of type {}", key);
        }
        return bean == null ? this.beanFactory.getBean(key) : bean;
    }
}
