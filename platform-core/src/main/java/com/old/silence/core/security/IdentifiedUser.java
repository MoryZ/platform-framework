package com.old.silence.core.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class IdentifiedUser implements IdentifiedUserDetails {

    private static final long serialVersionUID = -5735803175857874335L;
    private final Long id;

    private final UserDetails delegate;

    public IdentifiedUser(Long id, UserDetails delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate == null ? Collections.emptyList() : delegate.getAuthorities();
    }

    @Override
    public String getPassword() {
        return delegate == null ? null : delegate.getPassword();
    }

    @Override
    public String getUsername() {
        return delegate == null ? null : delegate.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return delegate == null || delegate.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return delegate == null || delegate.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return delegate == null || delegate.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return delegate == null || delegate.isEnabled();
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof IdentifiedUser) {
            return id.equals(((IdentifiedUser) rhs).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append(": ");
        builder.append("Id: ").append(this.id).append(";; ");
        builder.append("Username: ").append(delegate.getUsername()).append("; ");

        builder.append("Password: [PROTECTED]; ");
        builder.append("Enabled: ").append(delegate.isEnabled()).append("; ");
        builder.append("AccountNonExpired: ").append(delegate.isAccountNonExpired()).append("; ");
        builder.append("credentialsNonExpired: ").append(delegate.isCredentialsNonExpired()).append("; ");
        builder.append("AccountNonLocked: ").append(delegate.isAccountNonLocked()).append("; ");
        if (!delegate.getAuthorities().isEmpty()) {
            builder.append("Granted Authorities: ");
            boolean first = true;
            for (GrantedAuthority auth : delegate.getAuthorities()) {
                if (!first) {
                    builder.append(",");
                }
                first = false;
                builder.append(auth);
            }
        } else {
            builder.append("Not granted any authorities");
        }
        return builder.toString();
    }
    public static CustomUserBuilder withId(Long id) {
        return builder().id(id);
    }

    public static CustomUserBuilder builder() {
        return new CustomUserBuilder();
    }

    public static CustomUserBuilder withUserDetails(IdentifiedUserDetails userDetails) {
        return withId(userDetails.getId()).username(userDetails.getUsername()).password(userDetails.getPassword())
                .accountExpired(!userDetails.isAccountNonExpired()).accountLocked(!userDetails.isAccountNonLocked())
                .authorities(userDetails.getAuthorities()).credentialsExpired(!userDetails.isCredentialsNonExpired())
                .disabled(!userDetails.isEnabled());
    }

    public static class CustomUserBuilder {

        private final User.UserBuilder delegate;

        private Long id;

        private CustomUserBuilder() {
            this.delegate = User.builder();
        }

        public CustomUserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CustomUserBuilder username(String username) {
            delegate.username(username);
            return this;
        }

        public CustomUserBuilder password(String password) {
            delegate.password(password);
            return this;
        }

        public CustomUserBuilder roles(String... roles) {
            delegate.roles(roles);
            return this;
        }

        public CustomUserBuilder authorities(GrantedAuthority... authorities) {
            return authorities(Arrays.asList(authorities));
        }

        public CustomUserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            delegate.authorities(authorities);
            return this;
        }

        public CustomUserBuilder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        public CustomUserBuilder accountExpired(boolean accountExpired) {
            delegate.accountExpired(accountExpired);
            return this;
        }

        public CustomUserBuilder accountLocked(boolean accountLocked) {
            delegate.accountLocked(accountLocked);
            return this;
        }

        public CustomUserBuilder credentialsExpired(boolean credentialsExpired) {
            delegate.credentialsExpired(credentialsExpired);
            return this;
        }

        public CustomUserBuilder disabled(boolean disabled) {
            delegate.disabled(disabled);
            return this;
        }

        public IdentifiedUserDetails build() {
            UserDetails user = delegate.build();
            return new IdentifiedUser(id, user);
        }
    }
}
