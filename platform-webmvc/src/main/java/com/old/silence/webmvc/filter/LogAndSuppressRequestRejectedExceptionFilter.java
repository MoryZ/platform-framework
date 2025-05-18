package com.old.silence.webmvc.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
