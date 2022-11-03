package net.mmeany.play.blog.config.security;

import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakClientRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final Set<GrantedAuthority> ANONYMOUS = Set.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

    private final String keycloakClientId;

    public KeycloakClientRoleConverter(String keycloakClientId) {
        this.keycloakClientId = keycloakClientId;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        try {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            return resourceAccess == null || resourceAccess.isEmpty()
                    ? ANONYMOUS
                    : resourceAccess.entrySet().stream()
                    .filter(e -> keycloakClientId.equalsIgnoreCase(e.getKey()))
                    .map(e -> (JSONObject) e.getValue())
                    .map(o -> (List<String>) o.get("roles"))
                    .flatMap(Collection::stream)
                    .map(r -> "ROLE_" + r.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error obtaining claims from JWT", e);
        }
        return ANONYMOUS;
    }
}
