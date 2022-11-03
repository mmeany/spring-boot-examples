package net.mmeany.play.quote.config.security;

import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

@Slf4j
public class KeycloakClientRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public enum Mode {FROM_REALM, FROM_CLIENT, BOTH}

    private static final Set<GrantedAuthority> ANONYMOUS = Set.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

    private final String keycloakClientId;
    private final Mode mode;

    public KeycloakClientRoleConverter(String keycloakClientId, Mode mode) {
        this.keycloakClientId = keycloakClientId;
        this.mode = mode;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        switch (mode) {
            case FROM_REALM -> authorities.addAll(rolesFromRealmAccess(jwt));
            case FROM_CLIENT -> authorities.addAll(rolesFromResourceAccess(jwt));
            case BOTH -> {
                authorities.addAll(rolesFromRealmAccess(jwt));
                authorities.addAll(rolesFromResourceAccess(jwt));
            }
            default -> authorities.addAll(ANONYMOUS);
        }
        return authorities.isEmpty()
                ? ANONYMOUS
                : authorities;
    }

    public Collection<GrantedAuthority> rolesFromRealmAccess(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        try {
            Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                realmAccess.get("roles").stream()
                        .filter(Role::exists)
                        .map(Role::valueOf)
                        .forEach(r -> authorities.addAll(r.asGrantedAuthorities()));
            }
        } catch (Exception e) {
            log.error("Error obtaining claims from JWT", e);
        }
        return authorities;
    }

    public Collection<GrantedAuthority> rolesFromResourceAccess(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        try {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            if (resourceAccess != null) {
                resourceAccess.entrySet().stream()
                        .filter(e -> keycloakClientId.equalsIgnoreCase(e.getKey()))
                        .map(e -> (JSONObject) e.getValue())
                        .map(o -> (List<String>) o.get("roles"))
                        .flatMap(Collection::stream)
                        .filter(Role::exists)
                        .map(Role::valueOf)
                        .forEach(r -> authorities.addAll(r.asGrantedAuthorities()));
            }
        } catch (Exception e) {
            log.error("Error obtaining claims from JWT", e);
        }
        return authorities;
    }
}
