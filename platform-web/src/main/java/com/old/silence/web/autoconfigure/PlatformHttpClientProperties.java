package com.old.silence.web.autoconfigure;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(PlatformHttpClientProperties.PREFIX)
public class PlatformHttpClientProperties {
    public static final String PREFIX = "platform.httpclient";
    public static final boolean DEFAULT_DISABLE_SSL_VALIDATION = false;
    public static final int DEFAULT_MAX_CONNECTIONS = 200;
    public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 50;
    public static final Duration DEFAULT_TIME_TO_LIVE = Duration.ofMinutes(15L);
    public static final boolean DEFAULT_FOLLOW_REDIRECTS = true;
    public static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(10L);
    public static final Duration DEFAULT_CONNECTION_TIMER_REPEAT = Duration.ofSeconds(3L);
    private boolean disableSslValidation = DEFAULT_DISABLE_SSL_VALIDATION;
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
    private Duration timeToLive = DEFAULT_TIME_TO_LIVE;
    private boolean followRedirects = DEFAULT_FOLLOW_REDIRECTS;
    private Duration connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private Duration connectionTimerRepeat = DEFAULT_CONNECTION_TIMER_REPEAT;

    public boolean isDisableSslValidation() {
        return disableSslValidation;
    }

    public void setDisableSslValidation(boolean disableSslValidation) {
        this.disableSslValidation = disableSslValidation;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public Duration getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getConnectionTimerRepeat() {
        return connectionTimerRepeat;
    }

    public void setConnectionTimerRepeat(Duration connectionTimerRepeat) {
        this.connectionTimerRepeat = connectionTimerRepeat;
    }
}
