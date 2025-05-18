package com.old.silence.csv;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

@ConditionalOnClass(CsvMapper.class)
@ConditionalOnMissingBean(CsvMapper.class)
@AutoConfiguration(after = JacksonAutoConfiguration.class)
public class CsvMapperAutoConfiguration {

    @Bean
    CsvMapper csvMapper(Jackson2ObjectMapperBuilder builder, List<Jackson2CsvMapperBuilderCustomizer> customizers) {

        customize(builder, customizers);

        CsvMapper csvMapper = new CsvMapper();
        builder.configure(csvMapper);

        return csvMapper;
    }

    private void customize(Jackson2ObjectMapperBuilder builder, List<Jackson2CsvMapperBuilderCustomizer> customizers) {
        customizers.forEach(customizer -> customizer.customize(builder));
    }


    @Configuration(proxyBeanMethods = false)
    static class Jackson2CsvMapperBuilderCustomizerConfiguration {

        @Bean
        StandardJackson2CsvMapperBuilderCustomizer standardJacksonCsvMapperBuilderCustomizer() {
            return new StandardJackson2CsvMapperBuilderCustomizer();
        }

        static final class StandardJackson2CsvMapperBuilderCustomizer implements Jackson2CsvMapperBuilderCustomizer, Ordered {

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {

                builder.featuresToEnable(JsonGenerator.Feature.IGNORE_UNKNOWN)
                        .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                        .serializerByType(BigDecimal.class, new CsvBigDecimalSerializer())
                        .serializerByType(Instant.class, CsvInstantSerializer.INSTANCE).timeZone(TimeZone.getDefault())
                        .postConfigurer(mapper -> {
                            CsvMapper csvMapper = (CsvMapper) mapper;
                            csvMapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES).enable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
                                    .enable(CsvParser.Feature.EMPTY_STRING_AS_NULL);
                            csvMapper.disable(CsvParser.Feature.ALLOW_TRAILING_COMMA);
                        });
            }
        }
    }
}
