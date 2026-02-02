package com.old.silence.webmvc.data;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.old.silence.core.security.TenantContextAware;

/**
 * @author moryzang
 */
public class HttpHeaderTenantContextAware implements TenantContextAware<String> {

    @Override
    public Optional<String> getCurrentTenantId() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return Optional.empty();
        }
        HttpServletRequest request = attrs.getRequest();
        String tenantId = request.getHeader("X-Tenant-Id");
        return Optional.ofNullable(tenantId);
    }
}