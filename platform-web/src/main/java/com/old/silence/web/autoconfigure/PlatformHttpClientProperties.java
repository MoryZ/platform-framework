package com.old.silence.web.autoconfigure;

import java.time.Duration;

import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(PlatformHttpClientProperties.PREFIX)
public class PlatformHttpClientProperties {
    public static final String PREFIX = "platform.httpclient";
    public static final boolean DEFAULT_DISABLE_SSL_VALIDATION = false;

    public static final int DEFAULT_MAX_CONNECTIONS = 200;

    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 50;

    public static final Duration DEFAULT_TIME_TO_LIVE = Duration.ofMinutes(15L);

    public static final boolean DEFAULT_FOLLOW_REDIRECTS = true;

    public static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(3L);

    public static final Duration DEFAULT_CONNECTION_TIMER_REPEAT = Duration.ofSeconds(3L);

    public static final PoolConcurrencyPolicy DEFAULT_POOL_CONCURRENCY_POLICY = PoolConcurrencyPolicy.STRICT;

    public static final PoolReusePolicy DEFAULT_POOL_REUSE_POLICY = PoolReusePolicy.FIFO;

    public static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(5L);

    public static final Duration DEFAULT_CONNECTION_REQUEST_TIMEOUT = Duration.ofMinutes(3L);

    private boolean disableSslValidation = false;

    private int maxConnections = 200;

    private int maxConnectionsPerRoute = 50;

    private Duration timeToLive = DEFAULT_TIME_TO_LIVE;

    private boolean followRedirects = true;

    private Duration connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    private Duration connectionTimerRepeat = DEFAULT_CONNECTION_TIMER_REPEAT;

    private PoolConcurrencyPolicy poolConcurrencyPolicy = DEFAULT_POOL_CONCURRENCY_POLICY;

    private PoolReusePolicy poolReusePolicy = DEFAULT_POOL_REUSE_POLICY;

    private Duration socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    private Duration connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;

    public boolean isDisableSslValidation() {
        return this.disableSslValidation;
    }

    public void setDisableSslValidation(boolean disableSslValidation) {
        this.disableSslValidation = disableSslValidation;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return this.maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public Duration getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isFollowRedirects() {
        return this.followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getConnectionTimerRepeat() {
        return this.connectionTimerRepeat;
    }

    public void setConnectionTimerRepeat(Duration connectionTimerRepeat) {
        this.connectionTimerRepeat = connectionTimerRepeat;
    }

    public PoolConcurrencyPolicy getPoolConcurrencyPolicy() {
        return this.poolConcurrencyPolicy;
    }

    public void setPoolConcurrencyPolicy(PoolConcurrencyPolicy poolConcurrencyPolicy) {
        this.poolConcurrencyPolicy = poolConcurrencyPolicy;
    }

    public PoolReusePolicy getPoolReusePolicy() {
        return this.poolReusePolicy;
    }

    public void setPoolReusePolicy(PoolReusePolicy poolReusePolicy) {
        this.poolReusePolicy = poolReusePolicy;
    }

    public Duration getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(Duration socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Duration getConnectionRequestTimeout() {
        return this.connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Duration connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }
}
