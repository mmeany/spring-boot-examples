package net.mmeany.example.cucumberspring.config;

import lombok.extern.slf4j.Slf4j;
import net.mmeany.example.common.config.config.security.KeycloakClientRoleConverter;
import net.mmeany.example.common.config.config.security.Role;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    private static final String[] ANONYMOUS = {"ANONYMOUS"};

    private final ApplicationConfiguration applicationConfiguration;

    public SecurityConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Adding Security Filter Chain with: {}", applicationConfiguration.getKeycloak());
        return http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.name())
                .antMatchers("/swagger-ui/**").hasAnyRole(Role.SWAGGER.name())
                .antMatchers("/greet").permitAll()
                .antMatchers("/member/**").hasAnyRole(Role.MEMBER.name())
                .antMatchers("/manager/**").hasAnyRole(Role.MANAGER.name())
                .antMatchers("/admin/**").hasAnyRole(Role.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter()))
                )
                .build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtConverter() {
        log.info("Using conversion strategy '{}' for Keycloak client '{}'",
                applicationConfiguration.getKeycloak().getConversionStrategy(),
                applicationConfiguration.getKeycloak().getClientId());
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(
                new KeycloakClientRoleConverter(applicationConfiguration.getKeycloak().getClientId(),
                        applicationConfiguration.getKeycloak().getConversionStrategy())
        );
        return jwtConverter;
    }


    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-decoder-public-key-builder
    @Bean
    @ConditionalOnProperty(prefix = "app-config.keycloak", name = "jwt-mode", havingValue = "PUBLIC_KEY", matchIfMissing = false)
    public JwtDecoder jwtDecoder() throws Exception {
        log.info("JwtDecoder configured with public key: '{}'", applicationConfiguration.getKeycloak().getPublicKey());
        X509EncodedKeySpec ks = new X509EncodedKeySpec(
                Base64.getDecoder().decode(applicationConfiguration.getKeycloak().getPublicKey())
        );
        PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(ks);
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) pk).build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        log.info("Basic Auth Config with users: {}",
                applicationConfiguration.getUsers().stream().map(ApplicationConfiguration.UserRecord::username).toList());
        List<UserDetails> userDetails = applicationConfiguration.getUsers().stream()
                .map(u -> User.builder()
                        .username(u.username())
                        .password(u.encrypted())
                        .roles(u.roles() == null || u.roles().length == 0
                                ? ANONYMOUS
                                : Arrays.stream(u.roles())
                                .filter(Role::exists)
                                .map(Role::valueOf)
                                .map(Role::getRoles)
                                .flatMap(Collection::stream)
                                .map(Role::getRoles)
                                .flatMap(Collection::stream)
                                .map(Role::name)
                                .toArray(String[]::new)
                        )
                        .build())
                .toList();
        return new InMemoryUserDetailsManager(userDetails);
    }
}
