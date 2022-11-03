package net.mmeany.play.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null
                    ? Optional.of(auth.getName())
                    : Optional.of("SYSTEM");
        };
    }
}
