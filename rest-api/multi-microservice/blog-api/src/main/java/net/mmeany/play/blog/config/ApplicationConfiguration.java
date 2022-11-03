package net.mmeany.play.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mvm")
@Data
public class ApplicationConfiguration {
    private String allowedOrigins;
    private List<UserRecord> users;
    private KeycloakConfig keycloak;

    record UserRecord(String username,
                      String password,
                      String encrypted,
                      String[] roles) {
    }

    @Data
    public static class KeycloakConfig {
        private String clientId;
        private String publicKey;
    }
}
