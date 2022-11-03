package net.mmeany.example.common.config.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * This class provides a provider for Spring AuditorAware.
 * <p>
 * Annotate classes extending this with <code>@Configuration</code> and <code>@EnableJpaAuditing</code>
 */
public class BaseJpaConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null
                    ? Optional.of(auth.getName())
                    : Optional.of("SYSTEM (0)");
        };
    }
}
