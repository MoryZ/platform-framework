package com.old.silence.autoconfigure.validation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import com.old.silence.validation.beanvalidation.EnhancedSpringConstraintValidatorFactory;

@ConditionalOnBean(MessageSource.class)
@AutoConfiguration(before = ValidationAutoConfiguration.class)
public class CustomValidationAutoConfiguration {

    private CustomValidationAutoConfiguration() {
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static LocalValidatorFactoryBean defaultValidator(ApplicationContext applicationContext) {

        EnhancedSpringConstraintValidatorFactory constraintValidatorFactory = new EnhancedSpringConstraintValidatorFactory(
                applicationContext.getAutowireCapableBeanFactory());

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setConstraintValidatorFactory(constraintValidatorFactory);
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(applicationContext);
        validatorFactoryBean.setMessageInterpolator(interpolatorFactory.getObject());

        return validatorFactoryBean;
    }
}
