package com.old.silence.core.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

public interface IdentifiedUserDetails extends UserDetails {
    @Nullable
    Long getId();
}
