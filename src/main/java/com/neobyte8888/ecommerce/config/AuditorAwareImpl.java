package com.neobyte8888.ecommerce.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Lấy từ SecurityContext (Spring Security)
        return Optional.ofNullable(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }
}
