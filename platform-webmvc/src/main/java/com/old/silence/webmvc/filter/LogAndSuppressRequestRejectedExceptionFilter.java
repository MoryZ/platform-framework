package com.old.silence.webmvc.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


import org.springframework.http.HttpHeaders;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.filter.GenericFilterBean;

public class LogAndSuppressRequestRejectedExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (RequestRejectedException e) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            if (logger.isWarnEnabled()) {
                logger.warn(String.format("request_rejected: remote=%s, user_agent=%s, request_url=%s", request.getRemoteHost(),
                        request.getHeader(HttpHeaders.USER_AGENT), request.getRequestURL()), e);
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
