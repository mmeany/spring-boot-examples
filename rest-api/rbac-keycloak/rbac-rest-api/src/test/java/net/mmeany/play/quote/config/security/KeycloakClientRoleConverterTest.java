package net.mmeany.play.quote.config.security;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class KeycloakClientRoleConverterTest {

    public final String keycloakClientId = "blog_service";
    private final KeycloakClientRoleConverter rolesFromRealmSut = new KeycloakClientRoleConverter(keycloakClientId, KeycloakClientRoleConverter.Mode.FROM_REALM);
    private final KeycloakClientRoleConverter rolesFromResourceSut = new KeycloakClientRoleConverter(keycloakClientId, KeycloakClientRoleConverter.Mode.FROM_CLIENT);
    private final KeycloakClientRoleConverter rolesFromBothSut = new KeycloakClientRoleConverter(keycloakClientId, KeycloakClientRoleConverter.Mode.BOTH);

    @Test
    void obtainsAuthoritiesWhenResourcePresent() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", getBlogServiceResourceAccess())
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromResourceSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                containsInAnyOrder(Role.ADMIN.asGrantedAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()).toArray(String[]::new)));
    }

    @Test
    void obtainsAnonymousWhenResourceNotPresent() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", getOtherServiceResourceAccess())
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromResourceSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.size(), is(1));
        assertThat(grantedAuthorities.iterator().next().getAuthority(), is("ROLE_ANONYMOUS"));
    }

    @Test
    void obtainsAnonymousWhenRequiredResourceAccessNotPresent() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("no_resource_access", "true")
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromResourceSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.size(), is(1));
        assertThat(grantedAuthorities.iterator().next().getAuthority(), is("ROLE_ANONYMOUS"));
    }

    @Test
    void obtainsAnonymousWhenNoResourceAccessPresent() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", getNoServiceResourceAccess())
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromResourceSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.size(), is(1));
        assertThat(grantedAuthorities.iterator().next().getAuthority(), is("ROLE_ANONYMOUS"));
    }

    @Test
    void obtainsAdminWhenRealmAccessPresent() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", getAdminRealmAccess())
                .build();

        System.out.println(jwt.getTokenValue());

        Collection<GrantedAuthority> grantedAuthorities = rolesFromRealmSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                containsInAnyOrder(Role.ADMIN.asGrantedAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()).toArray(String[]::new)));
    }

    @Test
    void ignoreRealmRolesWhenModeResource() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", getBlogServiceResourceAccess())
                .claim("realm_access", getSwaggerRealmAccess())
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromResourceSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                containsInAnyOrder(Role.ADMIN.asGrantedAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()).toArray(String[]::new)));
    }

    @Test
    void ignoresResourceRoleWhenModeRealm() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", getBlogServiceResourceAccess())
                .claim("realm_access", getSwaggerRealmAccess())
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromRealmSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.size(), is(1));
        assertThat(grantedAuthorities.iterator().next().getAuthority(), is("ROLE_SWAGGER"));
    }

    @Test
    void getsAllRolesWhenModeBoth() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", getBlogServiceResourceAccess())
                .claim("realm_access", getSwaggerRealmAccess())
                .build();

        Collection<GrantedAuthority> grantedAuthorities = rolesFromBothSut.convert(jwt);
        assertThat(grantedAuthorities, notNullValue());
        assertThat(grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                containsInAnyOrder(
                        Stream.concat(
                                        Role.ADMIN.asGrantedAuthorities().stream(),
                                        Role.SWAGGER.asGrantedAuthorities().stream())
                                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()).toArray(String[]::new)));
    }

    JSONObject getBlogServiceResourceAccess() throws Exception {
        return new JSONObject(Map.of(
                "comment_service", new JSONObject(Map.of("roles", new JSONArray().appendElement("ADMIN"))),
                keycloakClientId, new JSONObject(Map.of("roles", new JSONArray().appendElement("ADMIN"))),
                "quote_service", new JSONObject(Map.of("roles", new JSONArray().appendElement("ADMIN")))
        ));
    }

    JSONObject getOtherServiceResourceAccess() throws Exception {
        return new JSONObject(Map.of(
                "other_service", new JSONObject(Map.of("roles", new JSONArray().appendElement("ADMIN")))
        ));
    }

    JSONObject getNoServiceResourceAccess() throws Exception {
        return new JSONObject(Map.of());
    }

    JSONObject getAdminRealmAccess() throws Exception {
        return new JSONObject(Map.of("roles", new JSONArray().appendElement("ADMIN").appendElement("ignored")));
    }

    JSONObject getManagerRealmAccess() throws Exception {
        return new JSONObject(Map.of("roles", new JSONArray().appendElement("MANAGER").appendElement("BULLY")));
    }

    JSONObject getMemberRealmAccess() throws Exception {
        return new JSONObject(Map.of("roles", new JSONArray().appendElement("MEMBER")));
    }

    JSONObject getSwaggerRealmAccess() throws Exception {
        return new JSONObject(Map.of("roles", new JSONArray().appendElement("SWAGGER")));
    }

    JSONObject getNoMappedRolesRealmAccess() throws Exception {
        return new JSONObject(Map.of("roles", new JSONArray().appendElement("NOT_MAPPED")));
    }
}