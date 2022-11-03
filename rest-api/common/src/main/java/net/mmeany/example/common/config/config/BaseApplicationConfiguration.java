package net.mmeany.example.common.config.config;

import lombok.Data;
import net.mmeany.example.common.config.config.security.KeycloakJwtDecodeStrategy;
import net.mmeany.example.common.config.config.security.KeycloakRoleResolutionStrategy;

import java.util.List;

/**
 * Application configuration store.
 * <p>
 * Classes extending from this should be annotated with <code>@ConfigurationProperties(prefix = "app-config")</code>
 * <p>
 * keycloak.public-key only need be provided if value of keycloak.jwt-mode is PUBLIC_KEY. Otherwise auth url is expected
 * to be configured in usual way at spring.security.oauth2.resourceserver.jwt.jwk-set-uri
 * <p>
 * Sample configuration block:
 * <pre>
 * app-config:
 *   allowedOrigins: https://mmeany.net,https://*.mmeany.net
 *   users:
 *     - username: mark
 *       password: _undisclosed_
 *       encrypted: "${BASIC_AUTH_PASSWORD:{noop}Password123}"
 *       roles: ADMIN
 *   keycloak:
 *     jwt-mode: JWKS
 *     client-id: blog-service
 *     public-key: MIIBIj..[snip]..IDAQAB
 *     conversion-strategy: BOTH
 * </pre>
 */
@Data
public class BaseApplicationConfiguration {
    /**
     * A comma delimited string of URLs to allow for CORS
     */
    private String allowedOrigins;
    /**
     * A list of users to configure for Basic Authentication
     */
    private List<UserRecord> users;
    /**
     * Keycloak configuration for disconnected decoding of JWTs
     */
    private KeycloakConfig keycloak;

    /**
     * Configuration used to build a User for Spring Security.
     *
     * @param username  Users username
     * @param password  Unencrypted password (optional, for test scenarios)
     * @param encrypted Encrypted password with scheme
     * @param roles     List of roles this user should be granted
     */
    public record UserRecord(String username,
                             String password,
                             String encrypted,
                             String[] roles) {
    }

    /**
     * Keycloak configuration.
     */
    @Data
    public static class KeycloakConfig {
        /**
         * Decode strategy to use, public key or JWKS
         */
        private KeycloakJwtDecodeStrategy jwtMode;
        /**
         * The Keycloak clients id, used to identify which resource in JWT to obtain roles from.
         */
        private String clientId;
        /**
         * The Keycloak servers Public Key, used to validate JWT signing
         */
        private String publicKey;

        /**
         * Conversion strategy to use. Determines where in the JWT roles are obtained from.
         */
        private KeycloakRoleResolutionStrategy conversionStrategy;
    }
}
