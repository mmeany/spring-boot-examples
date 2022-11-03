# Application Configuration

All application specific properties are loaded into [ApplicationConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/ApplicationConfiguration.java)
at launch. This is achieved using the mechanism prescribed in
the [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config.typesafe-configuration-properties).

In essence there is a block of configuration in [application.yml](../rbac-rest-api/src/main/resources/application.yml) that is
loaded into [ApplicationConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/ApplicationConfiguration.java) on launch.

```yaml
mvm:
  allowedOrigins: https://mmeany.net,https://www.mmeany.net
  users:
    - username: mark
      password: _undisclosed_
      encrypted: "${BASIC_AUTH_PASSWORD:{noop}Password123}"
      roles: ADMIN
  keycloak:
    client-id: blog-service
    # http://KEYCLOAK_SERVER_BASE_URL/realms/REALM
    public-key: MIIBI....
```

For Spring Boot to perform this mapping the appropriate annotation processor needs to be enabled, this is achieved using the
following entry in [build.gradle](../rbac-rest-api/build.gradle):

```groovy
    annotationProcessor "org.springframework.boot:spring-boot-configuration-processor"
```

And the following annotations on [ApplicationConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/ApplicationConfiguration.java):

```java

@Component
@ConfigurationProperties(prefix = "mvm")
public class ApplicationConfiguration {
    // ...
}
```

[ApplicationConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/ApplicationConfiguration.java) can be
injected into any bean now to make all application specific configuration available without the need to use `@Value` annotations.

# Spring Security Configuration

The security configuration used by the project can be found in [SecurityConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/SecurityConfiguration.java).
Specific details for each mechanism can be found below, but securing endpoints is done using the following:

```java
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/actuator/health").permitAll()
        .antMatchers("/actuator**").hasRole("ADMIN")
        .antMatchers("/swagger-ui/**").hasAnyRole("ADMIN","SWAGGER")
        .anyRequest().authenticated()
        .and()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic()
        .and()
        .oauth2ResourceServer(oauth2->oauth2
        .jwt(jwt->jwt
        .jwtAuthenticationConverter(keycloakJwtConverter())
        )
        );
        return http.build();
        }
```

* Stateless session management as this is an API
* Actuator is restricted to ADMIN users, except the `health` endpoint which is public
* The SWAGGER UI is restricted to ADMIN users or users with the `SWAGGER` role
* All endpoints require an authenticated user, none of them are public
* Disabled `csrf` and presenting a login form
* Enabled Basic Authentication - more below
* Enabled `JWT` authentication - more below

## Basic Authentication Configuration

[ApplicationConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/ApplicationConfiguration.java) contains a list
of user configurations that are used to configure an `InMemoryUserDetailsManager` exposed as a `Bean` in
[SecurityConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/SecurityConfiguration.java).

```java
    @Bean
public InMemoryUserDetailsManager userDetailsService(){
        List<UserDetails> userDetails=applicationConfiguration.getUsers().stream()
        .map(u->{
        System.out.println("Adding BasicAuth for user '"+u.username()+"' with password '"+u.password()+"', encrypted '"+u.encrypted()+"'");
        return User.builder()
        .username(u.username())
        .password(u.encrypted())
        .roles(u.roles())
        .build();
        })
        .toList();
        return new InMemoryUserDetailsManager(userDetails);
        }
```

## OAuth2 Configuration

Out of the box, the `resource-server` implementation wants to use a `JWKS` endpoint to communicate with Keycloak. That is not the
approach taken here, there were two specific requirements that necessitated further configuration:

* Make use of Client Roles, not Realm Roles.
* Remove the need to communicate with Keycloak and use a provided Public Key instead.

The first point warrants an explanation: Keycloak allows roles to be assigned to users globally, as Realm Roles or by client that
the user has access to as Client Roles. The location of the roles in the `JWT` varies depending on the type of role. For Client
Roles there is a `claim` in the `JWT` called `resource_access` and for each client the user has access to there is an entry
defining the roles for that client. This can be seen in the following fragment:

```json
{
  "resource_access": {
    "comment-service": {
      "roles": [
        "CREATE_COMMENT"
      ]
    },
    "blog-service": {
      "roles": [
        "CREATE_BLOG"
      ]
    },
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  }
}
```

Instructions for providing a role mapping implementation can be found in
the [Spring Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-sansboot),
and the one I created for extracting the roles in the class [KeycloakClientRoleConverter](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/security/KeycloakClientRoleConverter.java).

```java
    @Override
public Collection<GrantedAuthority> convert(Jwt jwt){

        try{
        Map<String, Object> resourceAccess=jwt.getClaimAsMap("resource_access");
        return resourceAccess==null
        ?Set.of()
        :resourceAccess.entrySet().stream()
        .filter(e->keycloakClientId.equalsIgnoreCase(e.getKey()))
        .map(e->(JSONObject)e.getValue())
        .map(o->(List<String>)o.get("roles"))
        .flatMap(Collection::stream)
        .map(r->"ROLE_"+r.toUpperCase())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
        }catch(Exception e){
        log.error("Error obtaining claims from JWT",e);
        }
        return Set.of();
        }
```

The second point was because this was a requirement I was investigating! Instructions from
the [Spring Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-decoder-public-key-builder)
and the `JwtDecoder` implementation can be found in the security configuration class
itself [SecurityConfiguration](../rbac-rest-api/src/main/java/net/mmeany/play/quote/config/SecurityConfiguration.java)

```java
    @Bean
public JwtDecoder jwtDecoder()throws Exception{
        X509EncodedKeySpec ks=new X509EncodedKeySpec(Base64.getDecoder().decode(applicationConfiguration.getKeycloak().getPublicKey()));
        PublicKey pk=KeyFactory.getInstance("RSA").generatePublic(ks);
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey)pk).build();
        }
```
