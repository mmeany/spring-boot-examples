package net.mmeany.example.common.config.config.security;

/**
 * Methods that can be adopted to extract roles from a JWT.
 */
public enum KeycloakRoleResolutionStrategy {
    /**
     * Extract roles from the 'realm_access' claim.
     */
    FROM_REALM,
    /**
     * Extract roles from the 'resource_access' claim that are client specific.
     */
    FROM_CLIENT,
    /**
     * Extract roles from both 'realm_access' and 'resource_access' claims
     */
    BOTH
}
