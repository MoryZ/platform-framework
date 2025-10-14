package com.old.silence.webmvc.autoconfigure;

import feign.FeignException;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.old.silence.core.enums.converter.EnumValueConverter;
import com.old.silence.csv.CsvMapperAutoConfiguration;
import com.old.silence.validation.beanvalidation.EnhancedSpringConstraintValidatorFactory;
import com.old.silence.webmvc.bind.GlobalBindingInitializer;
import com.old.silence.webmvc.embedded.PlatformTomcatWebServerFactoryCustomizer;
import com.old.silence.webmvc.exception.AccessDeniedExceptionHandler;
import com.old.silence.webmvc.exception.DuplicateKeyExceptionHandler;
import com.old.silence.webmvc.exception.FeignClientExceptionHandler;
import com.old.silence.webmvc.exception.GlobalMvcExceptionHandler;
import com.old.silence.webmvc.exception.MvcExceptionHandler;
import com.old.silence.webmvc.exception.MvcExceptionHandlerFactory;
import com.old.silence.webmvc.filter.LogAndSuppressRequestRejectedExceptionFilter;
import com.old.silence.webmvc.filter.MdcTraceIdFilter;
import com.old.silence.webmvc.validation.CollectionValidatorAdvice;

@ConditionalOnWebApplication
@Import(WebMvcCsvConfigurer.class)
@EnableConfigurationProperties(WebMvcProperties.class)
@AutoConfiguration(after = CsvMapperAutoConfiguration.class)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    private final WebMvcProperties properties;

    private final ApplicationContext applicationContext;

    public WebMvcAutoConfiguration(WebMvcProperties properties, ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    @Bean
    GlobalBindingInitializer globalBindingInitializer() {
        return new GlobalBindingInitializer();
    }

    @Bean
    CollectionValidatorAdvice collectionValidatorAdvice() {
        return new CollectionValidatorAdvice();
    }

    @Bean
    FilterRegistrationBean<CommonsRequestLoggingFilter> requestLoggingFilter() {

        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setMaxPayloadLength(1024);

        FilterRegistrationBean<CommonsRequestLoggingFilter> registration = new FilterRegistrationBean<>(loggingFilter);
        registration.addUrlPatterns(properties.getLoggingPathPatterns());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return registration;
    }

    @Bean
    @ConditionalOnProperty(name = "logging.logback.platform-tracing-output-enabled")
    FilterRegistrationBean<MdcTraceIdFilter> mdcTraceIdFilter() {

        FilterRegistrationBean<MdcTraceIdFilter> registration = new FilterRegistrationBean<>(new MdcTraceIdFilter());
        registration.addUrlPatterns(properties.getTraceIdPathPatterns());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);

        ConversionService conversionService = registry instanceof ConversionService ? (ConversionService) registry
                : DefaultConversionService.getSharedInstance();
        registry.addConverter(new EnumValueConverter(conversionService));
    }

    @Override
    public Validator getValidator() {

        EnhancedSpringConstraintValidatorFactory constraintValidatorFactory = new EnhancedSpringConstraintValidatorFactory(
                applicationContext.getAutowireCapableBeanFactory());

        OptionalValidatorFactoryBean validatorFactoryBean = new OptionalValidatorFactoryBean();
        validatorFactoryBean.setConstraintValidatorFactory(constraintValidatorFactory);
        validatorFactoryBean.setValidationMessageSource(applicationContext);
        validatorFactoryBean.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");

        return validatorFactoryBean;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(MessageSourceAccessor.class)
    static class ExceptionHandlerConfiguration {

        @Value("${platform.system.identifier}")
        private String systemIdentifier;

        @Bean
        @ConditionalOnClass(DuplicateKeyException.class)
        DuplicateKeyExceptionHandler duplicateKeyExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
            return new DuplicateKeyExceptionHandler(messageSourceAccessor);
        }

        @Bean
        @ConditionalOnClass(AccessDeniedException.class)
        AccessDeniedExceptionHandler accessDeniedExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
            return new AccessDeniedExceptionHandler(messageSourceAccessor);
        }

        @Bean
        @ConditionalOnClass(FeignException.FeignClientException.class)
        FeignClientExceptionHandler feignClientExceptionHandler() {
            return new FeignClientExceptionHandler();
        }

        @Bean
        MvcExceptionHandlerFactory mvcExceptionHandlerFactory(MessageSourceAccessor messageSourceAccessor,
                                                              ObjectProvider<MvcExceptionHandler> mvcExceptionHandlers) {
            return new MvcExceptionHandlerFactory(messageSourceAccessor, mvcExceptionHandlers);
        }

        @Bean
        GlobalMvcExceptionHandler globalMvcExceptionHandler(MvcExceptionHandlerFactory mvcExceptionHandlerFactory) {
            return new GlobalMvcExceptionHandler(mvcExceptionHandlerFactory, systemIdentifier);
        }
    }

    /**
     * Nested configuration if Tomcat is being used.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({Tomcat.class, UpgradeProtocol.class})
    public static class PlatformTomcatWebServerFactoryCustomizerConfiguration {

        @Bean
        PlatformTomcatWebServerFactoryCustomizer platformTomcatWebServerFactoryCustomizer() {
            return new PlatformTomcatWebServerFactoryCustomizer();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(HttpFirewall.class)
    static class LogAndSuppressRequestRejectedExceptionFilterConfiguration {

        @Bean
        LogAndSuppressRequestRejectedExceptionFilter logAndSuppressRequestRejectedExceptionFilter() {
            return new LogAndSuppressRequestRejectedExceptionFilter();
        }

        @Bean
        FilterRegistrationBean<LogAndSuppressRequestRejectedExceptionFilter> logAndSuppressRequestRejectedExceptionFilterRegistration() {

            FilterRegistrationBean<LogAndSuppressRequestRejectedExceptionFilter> registration = new FilterRegistrationBean<>(
                    logAndSuppressRequestRejectedExceptionFilter());
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

            return registration;
        }
    }
}
