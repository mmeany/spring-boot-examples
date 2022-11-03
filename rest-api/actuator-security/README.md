# Configuring security on Actuator endpoints

Just an example of applying what's in the [Spring Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#actuator.endpoints.security).

The imports required in [build.gradle](./build.gradle):

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

And setting the security to require an `ADMIN` role for any Actuator endpoint accept `/actuator/health`,
see [SecurityConfig](./src/main/java/net/mmeany/play/actuatorsecurity/config/SecurityConfig.java):

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic();
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails[] users = new UserDetails[3];
        users[0] = User.builder()
                .username("test-user-1")
                .password("{noop}Password123")
                .roles("ADMIN")
                .build();
        users[1] = User.builder()
                .username("test-user-2")
                .password("{noop}Password123")
                .roles("CREATOR")
                .build();
        users[2] = User.builder()
                .username("test-user-3")
                .password("{noop}Password123")
                .roles("MEMBER")
                .build();
        return new InMemoryUserDetailsManager(users);
    }
}
```

With the above config only `test-user-1` can access the restricted actuator endpoints as its the only user with `ADMIN` role.

