package com.old.silence.web.autoconfigure;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import com.old.silence.core.security.SecurityUtils;

@ConditionalOnClass({CloseableHttpClient.class})
@ConditionalOnMissingBean({CloseableHttpClient.class})
@EnableConfigurationProperties({PlatformHttpClientProperties.class})
@AutoConfiguration(
        afterName = {"org.springframework.cloud.openfeign.loadbalancer.FeignLoadBalancerAutoConfiguration",
                "org.springframework.cloud.openfeign.FeignAutoConfiguration"}
)
public class HttpClientAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    private final PlatformHttpClientProperties properties;
    private final Timer connectionManagerTimer = new Timer("HttpClientAutoConfiguration.connectionManagerTimer", true);
    private CloseableHttpClient httpClient;
    public HttpClientAutoConfiguration(PlatformHttpClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean({HttpClientConnectionManager.class})
    HttpClientConnectionManager connectionManager(ObjectProvider<RegistryBuilder<ConnectionSocketFactory>> registryBuilderProvider) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = (RegistryBuilder) registryBuilderProvider.getIfAvailable(() -> {
            RegistryBuilder<ConnectionSocketFactory> objectRegistryBuilder = RegistryBuilder.create();
            return objectRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        });
        if (this.properties.isDisableSslValidation()) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init((KeyManager[])null, new TrustManager[]{new DisabledValidationTrustManager()}, SecurityUtils.getSecureRandomInstance());
                registryBuilder.register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE));
            } catch (GeneralSecurityException var5) {
                logger.warn("Error creating SSLContext", var5);
            }
        } else {
            registryBuilder.register("https", SSLConnectionSocketFactory.getSocketFactory());
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry, (HttpConnectionFactory)null, (SchemePortResolver)null, (DnsResolver)null, this.properties.getTimeToLive().toMillis(), TimeUnit.MILLISECONDS);
        connectionManager.setMaxTotal(this.properties.getMaxConnections());
        connectionManager.setDefaultMaxPerRoute(this.properties.getMaxConnectionsPerRoute());
        this.connectionManagerTimer.schedule(new TimerTask() {
            public void run() {
                connectionManager.closeExpiredConnections();
            }
        }, 30000L, this.properties.getConnectionTimerRepeat().toMillis());
        return connectionManager;
    }

    @Bean
    @ConditionalOnProperty(
            value = {"platform.httpclient.compression.response.enabled"},
            havingValue = "true"
    )
    CloseableHttpClient customHttpClient(HttpClientConnectionManager httpClientConnectionManager) {
        HttpClientBuilder builder = createDefaultBuilder();
        this.httpClient = this.createClient(builder, httpClientConnectionManager);
        return this.httpClient;
    }
    @Bean
    @ConditionalOnProperty(
            value = {"platform.httpclient.compression.response.enabled"},
            havingValue = "false",
            matchIfMissing = true
    )
    CloseableHttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager) {
        HttpClientBuilder builder = createDefaultBuilder().disableContentCompression();
        this.httpClient = this.createClient(builder, httpClientConnectionManager);
        return this.httpClient;
    }

    private static HttpClientBuilder createDefaultBuilder() {
        return HttpClientBuilder.create().disableCookieManagement().useSystemProperties();
    }

    private CloseableHttpClient createClient(HttpClientBuilder builder, HttpClientConnectionManager httpClientConnectionManager) {
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(Math.toIntExact(this.properties.getConnectionTimeout().toMillis())).setRedirectsEnabled(this.properties.isFollowRedirects()).build();
        return builder.setDefaultRequestConfig(defaultRequestConfig).setConnectionManager(httpClientConnectionManager).build();
    }

    @PreDestroy
    public void destroy() {
        this.connectionManagerTimer.cancel();
        if (this.httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException var2) {
                logger.error("Could not correctly close httpClient.", var2);
            }
        }

    }

    private static class DisabledValidationTrustManager implements X509TrustManager {
        private DisabledValidationTrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
