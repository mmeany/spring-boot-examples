package net.mmeany.example.common.config.config.security;

/**
 * Methods that can be adopted to decode and validate a JWT.
 */
public enum KeycloakJwtDecodeStrategy {
    /**
     * Use provided public key to decode and validate JWTs
     */
    PUBLIC_KEY,
    /**
     * Use provided URL to obtain public keys for decoding JWTs
     */
    JWKS
}
