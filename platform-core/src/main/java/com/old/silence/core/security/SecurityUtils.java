package com.old.silence.core.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static final String ROLE_PREFIX = "ROLE_";

    private SecurityUtils() {
        throw new AssertionError();
    }

    public static SecureRandom getSecureRandomInstance() {
        try {
            return SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException e) {
            // ignore
        }
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            return new SecureRandom();
        }
    }

    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .flatMap(context -> Optional.ofNullable(context.getAuthentication()));
    }
    public static <T> Optional<T> getCurrentAuthenticationDetails(Class<T> type) {
        return getCurrentAuthentication().map(Authentication::getDetails)
                .filter(details -> details != null && type.isAssignableFrom(details.getClass())).map(type::cast);
    }

    public static <T> Optional<T> getCurrentPrincipal(Class<T> type) {
        return getCurrentAuthentication().map(Authentication::getPrincipal)
                .filter(principal -> principal != null && type.isAssignableFrom(principal.getClass())).map(type::cast);
    }

    public static Optional<Long> getCurrentUserId() {
        return getCurrentAuthentication().map(SecurityUtils::getPrincipalIdentifier);
    }
    public static Long getPrincipalIdentifier(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        if (authentication.getPrincipal() instanceof IdentifiedUserDetails) {
            return ((IdentifiedUserDetails) authentication.getPrincipal()).getId();
        }

        String name = authentication.getName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (!NumberUtils.isParsable(name)) {
            return null;
        }
        return Long.valueOf(name);
    }
}
