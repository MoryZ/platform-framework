package com.old.silence.web.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class RestClientResponseErrorHandler extends DefaultResponseErrorHandler {

    public RestClientResponseErrorHandler() {
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() != HttpStatus.NOT_FOUND) {
            super.handleError(response);
        }
    }
}
