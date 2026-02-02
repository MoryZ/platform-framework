package com.old.silence.web.autoconfigure;

import jakarta.annotation.PreDestroy;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.old.silence.core.security.SecurityUtils;

/**
 * @author MurrayZhang
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CloseableHttpClient.class})
@ConditionalOnMissingBean({CloseableHttpClient.class})
@EnableConfigurationProperties({PlatformHttpClientProperties.class})
@AutoConfiguration(afterName = {"org.springframework.cloud.openfeign.loadbalancer.FeignLoadBalancerAutoConfiguration", "org.springframework.cloud.openfeign.FeignAutoConfiguration"})
public class HttpClient5AutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient5AutoConfiguration.class);

    private CloseableHttpClient httpClient5;

    @Bean
    @ConditionalOnMissingBean({HttpClientConnectionManager.class})
    HttpClientConnectionManager hc5ConnectionManager(PlatformHttpClientProperties httpClientProperties) {
        PoolConcurrencyPolicy poolConcurrencyPolicy = PoolConcurrencyPolicy.valueOf(httpClientProperties
                .getPoolConcurrencyPolicy().name());
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setConnectTimeout(Timeout.of(httpClientProperties.getConnectionTimeout())).setTimeToLive(TimeValue.of(httpClientProperties.getTimeToLive())).build();
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(Timeout.of(httpClientProperties.getSocketTimeout())).build();
        return (HttpClientConnectionManager) PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(httpsSSLConnectionSocketFactory(httpClientProperties.isDisableSslValidation()))
                .setMaxConnTotal(httpClientProperties.getMaxConnections())
                .setMaxConnPerRoute(httpClientProperties.getMaxConnectionsPerRoute())
                .setConnPoolPolicy(PoolReusePolicy.valueOf(httpClientProperties.getPoolReusePolicy().name()))
                .setPoolConcurrencyPolicy(poolConcurrencyPolicy).setDefaultConnectionConfig(connectionConfig)
                .setDefaultSocketConfig(socketConfig).build();
    }

    private LayeredConnectionSocketFactory httpsSSLConnectionSocketFactory(boolean isDisableSslValidation) {
        SSLConnectionSocketFactoryBuilder sslConnectionSocketFactoryBuilder = SSLConnectionSocketFactoryBuilder.create().setTlsVersions(new TLS[] { TLS.V_1_3, TLS.V_1_2 });
        if (isDisableSslValidation) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[] { new DisabledValidationTrustManager() }, SecurityUtils.getSecureRandomInstance());
                sslConnectionSocketFactoryBuilder.setSslContext(sslContext);
                sslConnectionSocketFactoryBuilder.setHostnameVerifier((HostnameVerifier) NoopHostnameVerifier.INSTANCE);
            } catch (GeneralSecurityException e) {
                logger.warn("Error creating SSLContext", e);
            }
        } else {
            sslConnectionSocketFactoryBuilder.setSslContext(SSLContexts.createSystemDefault());
        }
        return (LayeredConnectionSocketFactory)sslConnectionSocketFactoryBuilder.build();
    }

    @Bean
    CloseableHttpClient httpClient5(HttpClientConnectionManager connectionManager, PlatformHttpClientProperties httpClientProperties, ObjectProvider<List<HttpClientBuilderCustomizer>> customizerProvider) {
        RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(httpClientProperties.isFollowRedirects()).setConnectionRequestTimeout(Timeout.of(httpClientProperties.getConnectionRequestTimeout())).build();
        HttpClientBuilder httpClientBuilder = HttpClients.custom().disableCookieManagement().useSystemProperties().setConnectionManager(connectionManager).evictExpiredConnections().setDefaultRequestConfig(requestConfig);
        customizerProvider.getIfAvailable(List::of).forEach(c -> c.customize(httpClientBuilder));
        this.httpClient5 = httpClientBuilder.build();
        return this.httpClient5;
    }

    @PreDestroy
    public void destroy() {
        if (this.httpClient5 != null) {
            this.httpClient5.close(CloseMode.GRACEFUL);
        }
    }

    static class DisabledValidationTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public interface HttpClientBuilderCustomizer {
        void customize(HttpClientBuilder param1HttpClientBuilder);
    }
}
