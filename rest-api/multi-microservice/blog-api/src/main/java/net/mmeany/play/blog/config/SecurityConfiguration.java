package net.mmeany.play.blog.config;

import net.mmeany.play.blog.config.security.KeycloakClientRoleConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
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
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] ANONYMOUS = {"ANONYMOUS"};

    private final ApplicationConfiguration applicationConfiguration;

    public SecurityConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/blog**").hasAnyRole("ADMIN", "CREATE_BLOG")
                .antMatchers("/swagger-ui/**").hasAnyRole("ADMIN", "SWAGGER")
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(keycloakJwtConverter())
                        )
                )
                .build();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakClientRoleConverter(applicationConfiguration.getKeycloak().getClientId()));
        return jwtConverter;
    }


    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-decoder-public-key-builder
    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(Base64.getDecoder().decode(applicationConfiguration.getKeycloak().getPublicKey()));
        PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(ks);
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) pk).build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        List<UserDetails> userDetails = applicationConfiguration.getUsers().stream()
                .map(u -> User.builder()
                        .username(u.username())
                        .password(u.encrypted())
                        .roles(u.roles() == null || u.roles().length == 0 ? ANONYMOUS : u.roles())
                        .build())
                .toList();
        return new InMemoryUserDetailsManager(userDetails);
    }

}
