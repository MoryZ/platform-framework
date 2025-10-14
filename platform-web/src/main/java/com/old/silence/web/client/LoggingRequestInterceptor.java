package com.old.silence.web.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import com.old.silence.core.util.CollectionUtils;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
    private static final String[] DEFAULT_OUTPUT_RESPONSE_HEADER = new String[]{"Content-Type"};
    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 1024;
    private boolean includeRequestHeader = false;
    private boolean includeResponseHeader = false;
    private int maxPayloadLength = 1024;

    public LoggingRequestInterceptor() {
    }

    public void setIncludeRequestHeader(boolean includeRequestHeader) {
        this.includeRequestHeader = includeRequestHeader;
    }

    public void setIncludeResponseHeader(boolean includeResponseHeader) {
        this.includeResponseHeader = includeResponseHeader;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (logger.isDebugEnabled()) {
            this.logRequest(request, body);
        }

        ClientHttpResponse response = execution.execute(request, body);
        if (logger.isDebugEnabled()) {
            response = new BufferingClientHttpResponseWrapper((ClientHttpResponse) response);
            this.logResponse((ClientHttpResponse) response);
        }

        return (ClientHttpResponse) response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        logger.debug("http-uri >> \"{} {}\"", request.getMethod(), request.getURI());
        if (this.includeRequestHeader) {
            logHeader(request, ">>");
        }

        String payload = "";
        if (body.length > 0) {
            int length = Math.min(body.length, this.maxPayloadLength);
            payload = new String(body, 0, length, StandardCharsets.UTF_8);
        }

        logger.debug("http-body >> \"{}\"", payload);
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (StringUtils.isEmpty(response.getStatusText())) {
            logger.debug("http-response << \"{}\"", response.getStatusCode().value());
        } else {
            logger.debug("http-response << \"{} {}\"", response.getStatusCode().value(), response.getStatusText());
        }

        int length;
        if (this.includeResponseHeader) {
            logHeader(response, "<<");
        } else {
            String[] var2 = DEFAULT_OUTPUT_RESPONSE_HEADER;
            int var3 = var2.length;

            for (length = 0; length < var3; ++length) {
                String headerName = var2[length];
                List<String> values = response.getHeaders().get(headerName);
                if (CollectionUtils.isNotEmpty(values)) {
                    logHeader(headerName, values, "<<");
                }
            }
        }

        String payload = "";
        byte[] buf = StreamUtils.copyToByteArray(response.getBody());
        if (buf.length > 0) {
            length = Math.min(buf.length, this.maxPayloadLength);
            payload = new String(buf, 0, length, StandardCharsets.UTF_8);
        }

        logger.debug("http-body << \"{}\"", payload);
    }

    private static void logHeader(HttpMessage message, String direction) {
        Iterator var2 = message.getHeaders().entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry) var2.next();
            logHeader((String) entry.getKey(), (List) entry.getValue(), direction);
        }

    }

    private static void logHeader(String key, List<String> values, String direction) {
        if (logger.isDebugEnabled()) {
            logger.debug("http-header {} \"{}: {}\"", new Object[]{direction, key, String.join(", ", values)});
        }

    }

    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;

        private byte[] body;

        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }

        public HttpStatusCode getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }

        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        public InputStream getBody() throws IOException {
            if (this.body == null)
                this.body = this.response.getBody().readAllBytes();
            return new ByteArrayInputStream(this.body);
        }

        public void close() {
            this.response.close();
        }
    }
}
