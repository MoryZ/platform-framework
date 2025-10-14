package com.old.silence.webmvc.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;


import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public class MdcTraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "platform.traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        MDC.put(TRACE_ID_KEY, UUID.randomUUID().toString());
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
