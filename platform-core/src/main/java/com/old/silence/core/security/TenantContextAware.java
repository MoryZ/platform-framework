package com.old.silence.core.security;

import java.util.Optional;

/**
 * @author moryzang
 */
public interface TenantContextAware<ID> {

    /**
     * 返回当前请求对应的租户ID
     */
    Optional<ID> getCurrentTenantId();
}
