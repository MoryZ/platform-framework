package com.old.silence.autoconfigure.jdbc;

import oracle.ucp.jdbc.PoolDataSourceImpl;

import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import com.old.silence.core.condition.ConditionOnPropertyPrefix;
import com.old.silence.core.util.CollectionUtils;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author murrayZhang
 */
@AutoConfiguration(before = {DataSourceAutoConfiguration.class, SqlInitializationAutoConfiguration.class})
@ConditionOnPropertyPrefix("spring.datasource.routing")
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
@Import(DataSourcePoolMetadataProvider.class)
@EnableConfigurationProperties(RoutingDataSourceProperties.class)
public class RoutingDataSourceAutoConfiguration {

    private static final String DATA_SOURCE_PREFIX = "spring.datasource.";

    private static final String DATA_SOURCE_ROUTINGS_PREFIX = DATA_SOURCE_PREFIX;

    private static final String DEFAULT_EXTENSION_NAME = "default";

    @Bean
    @ConditionalOnClass(HikariDataSource.class)
    HikariDataSourceFactory hikariDataSourceFactory() {
        return new HikariDataSourceFactory();
    }

    @Bean
    @ConditionalOnClass(org.apache.tomcat.jdbc.pool.DataSource.class)
    TomcatDataSourceFactory tomcatDataSourceFactory() {
        return new TomcatDataSourceFactory();
    }

    @Bean
    @ConditionalOnClass(BasicDataSource.class)
    Dbcp2DataSourceFactory dbcp2DataSourceFactory() {
        return new Dbcp2DataSourceFactory();
    }

    @Bean
    @ConditionalOnClass(PoolDataSourceImpl.class)
    OracleDataSourceFactory oracleDataSourceFactory() {
        return new OracleDataSourceFactory();
    }

    @Bean
    GenericDataSourceFactory genericDataSourceFactory() {
        return new GenericDataSourceFactory();
    }

    @Bean
    @ConditionalOnMissingBean({DataSource.class, XADataSource.class})
    DynamicDataSourceRouter routingDataSource(RoutingDataSourceProperties properties,
                                              ObjectProvider<PooledDataSourceFactory<?>> dataSourceFactoryObjectProvider, Environment environment) {
        Map<Class<? extends DataSource>, PooledDataSourceFactory<?>> dataSourceFactories = dataSourceFactoryObjectProvider.stream().collect(Collectors.toMap(PooledDataSourceFactory::getSupportedType, Function.identity()));
        DynamicDataSourceRouter router = new DynamicDataSourceRouter();
        Map<Object, Object> targetDataSources = CollectionUtils.transformToMap(properties.getRoutings().entrySet(), Map.Entry::getKey,
                entry -> {
                    try {
                        return createDataSource(entry.getKey(), entry.getValue(), dataSourceFactories, environment);
                    } catch (SQLException e) {
                        throw new UndeclaredThrowableException(e);
                    }
                });
        router.setTargetDataSources(targetDataSources);
        router.setDefaultTargetDataSource(targetDataSources.get(DEFAULT_EXTENSION_NAME));

        return router;
    }

    private DataSource createDataSource(String routingName, DataSourceProperties properties,
                                        Map<Class<? extends DataSource>, PooledDataSourceFactory<?>> dataSourceFactories, Environment environment)
            throws SQLException {
        PooledDataSourceFactory<?> factory = getDataSourceFactory(properties, dataSourceFactories);
        DataSource dataSource = factory.createDateSource(properties);

        bindExtensionProperty(routingName, factory.getExtensionName(), dataSource, environment);

        return dataSource;
    }

    private PooledDataSourceFactory<?> getDataSourceFactory(DataSourceProperties properties,
                                                            Map<Class<? extends DataSource>, PooledDataSourceFactory<?>> dataSourceFactories) {
        Class<? extends DataSource> type = properties.getType();
        if (type != null) {
            return dataSourceFactories.getOrDefault(type, dataSourceFactories.get(DataSource.class));
        }

        type = DataSourceBuilder.findType(ClassUtils.getDefaultClassLoader());
        PooledDataSourceFactory<?> factory = dataSourceFactories.get(type);
        return Objects.requireNonNull(factory, "Unable to find available PooledDataSourceFactory by type [" + type + "]");
    }

    private static void bindExtensionProperty(String routingName, String extensionName, DataSource dataSource,
                                              Environment environment) {
        Binder binder = Binder.get(environment);
        Bindable<DataSource> bindable = Bindable.ofInstance(dataSource);

        Stream.of(DATA_SOURCE_PREFIX + extensionName, getBindingName(routingName, extensionName)).filter(StringUtils::isNotEmpty)
                .forEach(bindingName -> binder.bind(bindingName, bindable));
    }

    private static String getBindingName(String name, String extensionName) {
        return StringUtils.isBlank(extensionName) ? null : DATA_SOURCE_ROUTINGS_PREFIX + name + "." + extensionName;
    }


}
