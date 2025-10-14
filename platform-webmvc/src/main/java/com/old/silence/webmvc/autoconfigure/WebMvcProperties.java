package com.old.silence.webmvc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "platform.mvc")
public class WebMvcProperties {

    private String[] loggingPathPatterns = { "/*" };

    private String[] traceIdPathPatterns = { "/*" };

    public String[] getLoggingPathPatterns() {
        return loggingPathPatterns;
    }

    public void setLoggingPathPatterns(String[] loggingPathPatterns) {
        this.loggingPathPatterns = loggingPathPatterns;
    }

    public String[] getTraceIdPathPatterns() {
        return traceIdPathPatterns;
    }

    public void setTraceIdPathPatterns(String[] traceIdPathPatterns) {
        this.traceIdPathPatterns = traceIdPathPatterns;
    }
}
