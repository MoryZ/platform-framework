package com.old.silence.webmvc.data;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.old.silence.core.security.UserContextAware;

/**
 * @author moryzang
 */
public class UserHeaderAuditorAware implements UserContextAware<String> {

    private final Optional<String> defaultAuditorAwareName;

    public UserHeaderAuditorAware(String defaultAuditorAwareName) {
        this.defaultAuditorAwareName = Optional.of(defaultAuditorAwareName);
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return this.getDefaultAuditorAwareName();
        } else {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String headerUsername = request.getHeader("X-Platform-User-Header");
            return StringUtils.isBlank(headerUsername) ? this.getDefaultAuditorAwareName() : Optional.of(headerUsername);
        }
    }

    private Optional<String> getDefaultAuditorAwareName() {
        return this.defaultAuditorAwareName;
    }
}
