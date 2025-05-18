package com.old.silence.webmvc.exception;

import feign.FeignException;
import feign.FeignException.FeignClientException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

public class FeignClientExceptionHandler extends AbstractMvcExceptionHandler {

    public FeignClientExceptionHandler() {
        super(FeignException.FeignClientException.class);
    }

    @Override
    public ResponseEntity<?> handle(Throwable feignClientException, WebRequest request, String systemIdentifier) { // NOSONAR

        FeignClientException exception = (FeignClientException) feignClientException;
        HttpStatus status = HttpStatus.valueOf(exception.status());

        logException(status, exception.contentUTF8(), exception);

        NativeWebRequest nativeRequest = (NativeWebRequest) request;
        HttpServletResponse response = nativeRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            return null;
        }

        response.setStatus(status.value());
        exception.responseBody().flatMap(FeignClientExceptionHandler::toBytes).ifPresent(bytes -> {
            try {
                ServletOutputStream output = response.getOutputStream();
                output.write(bytes);
                output.flush();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        return null;
    }

    private static Optional<byte[]> toBytes(ByteBuffer buffer) {

        if (!buffer.hasRemaining()) {
            return Optional.empty();
        }

        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }

        return ArrayUtils.isEmpty(bytes) ? Optional.empty() : Optional.of(bytes);
    }

}
