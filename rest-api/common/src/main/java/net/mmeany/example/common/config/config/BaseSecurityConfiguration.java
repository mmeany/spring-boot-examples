package net.mmeany.example.common.config.config;

import lombok.extern.slf4j.Slf4j;
import net.mmeany.example.common.config.config.security.KeycloakClientRoleConverter;
import net.mmeany.example.common.config.config.security.KeycloakJwtDecodeStrategy;
import net.mmeany.example.common.config.config.security.Role;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
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

/**
 * Annotate classes extending this with <code>@Configuration</code> and <code>@EnableWebSecurity</code>
 */
@Slf4j
public class BaseSecurityConfiguration {

    private static final String[] ANONYMOUS = {"ANONYMOUS"};

    private final BaseApplicationConfiguration applicationConfiguration;

    public BaseSecurityConfiguration(BaseApplicationConfiguration applicationConfiguration) {
        log.info("MVM: Configuring security with: {}", applicationConfiguration);
        this.applicationConfiguration = applicationConfiguration;
    }

    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("MVM:---------------------------------------------- Filter Chain with: {}", applicationConfiguration);
        HttpSecurity httpSecurity = http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.name())
                .antMatchers("/swagger-ui/**").hasAnyRole(Role.SWAGGER.name())
                .antMatchers("/member/**").hasAnyRole(Role.MEMBER.name())
                .antMatchers("/manager/**").hasAnyRole(Role.MANAGER.name())
                .antMatchers("/admin/**").hasAnyRole(Role.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic()
                .and();

        if (applicationConfiguration.getKeycloak().getJwtMode() == KeycloakJwtDecodeStrategy.PUBLIC_KEY) {
            httpSecurity.oauth2ResourceServer(
                    oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter())));
        } else {
            httpSecurity.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        }

        return http.build();
    }

    // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
    protected Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtConverter() {
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
    @ConditionalOnExpression("#{ applicationConfiguration.keycloak != null && applicationConfiguration.keycloak.publicKey != null }")
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
        log.info("MVM:---------------------------------------------- Filter Chain with: {}", applicationConfiguration);
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
