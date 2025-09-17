package com.old.silence.core.security;

import java.util.Optional;

/**
 * @author moryzang
 */
public interface UserContextAware<T> {

    /**
     * 获取当前用户名
     * @return 当前用户名，如果没有认证用户则返回默认值
     */
    Optional<T> getCurrentAuditor();
}
