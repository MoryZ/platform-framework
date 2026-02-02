package com.old.silence.webmvc.autoconfigure;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.old.silence.http.converter.MappingJackson2CsvHttpMessageConverter;
import com.old.silence.http.converter.support.CsvFilenameGenerator;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(CsvMapper.class)
@ConditionalOnBean(CsvMapper.class)
class WebMvcCsvConfigurer implements WebMvcConfigurer {

    private final CsvMapper csvMapper;

    private final Optional<CsvFilenameGenerator> csvFilenameGeneratorProvider;

    public WebMvcCsvConfigurer(CsvMapper csvMapper, Optional<CsvFilenameGenerator> csvFilenameGeneratorProvider) {
        this.csvMapper = csvMapper;
        this.csvFilenameGeneratorProvider = csvFilenameGeneratorProvider;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2CsvHttpMessageConverter(csvMapper, csvFilenameGeneratorProvider));
    }
}
