package net.mmeany.example.config;

import net.mmeany.example.config.security.Role;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] ANONYMOUS = {"ANONYMOUS"};

    private final ApplicationConfiguration applicationConfiguration;

    public SecurityConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-sansboot
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .formLogin().disable();

        // Apply basic auth only if security is enabled
        return applicationConfiguration.getSecurityEnabled()
                ? httpSecurity.httpBasic()
                .and()
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN.name())
                .antMatchers("/swagger-ui/**").hasAnyRole(Role.SWAGGER.name())
                .antMatchers("/quote/member").hasAnyRole(Role.MEMBER.name())
                .antMatchers("/quote/manager").hasAnyRole(Role.MANAGER.name())
                .antMatchers("/quote/admin").hasAnyRole(Role.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .build()

                : httpSecurity.authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
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
