# Introduction

Securing an API two ways, using Basic Authentication and JWT Authentication using Spring Security Resource Server.

There is a Keycloak adapter that can be used to configure Spring Security, however the intention here is to use out-of-the-box
components as much as possible. Also, at the time this project was being written, the Keycloak adapter was using deprecated
`WebSecurityConfigurerAdapter` and was proving irksome to get working with Basic Authentication at the same time.

Since both Basic Auth and OAuth are provided the API can be accessed without a Keycloak instance or using `JWT` at all.
That sort of defeats the purpose though.

# Keycloak

Use the Docker container started from the `docker/compose/mvm` directory, it comes with a pre-configured `dev` realm.
More information about this container can be found [in the README](../../docker/compose/mvm/README.md#Keycloak).

# The API

As per all examples the API is split into to modules, one for the API and one for the models returned by the API.

There is a single endpoint in this API, it returns a random quote and uses [DataFaker]() to generate these. The
code is fairly minimal:

```java
    @Operation(summary = "Fetch a random quote")
@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<QuoteDto> quote(){
        dumpAuthorities("quote()");
        return ResponseEntity.ok(quoteService.nextQuote());
        }

private void dumpAuthorities(String method){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String username="Unknown";
        Object principal=auth.getPrincipal();
        if(principal instanceof Jwt jwt){
        username=jwt.getClaim("preferred_username");
        }
        if(principal instanceof User user){
        username=user.getUsername();
        }
        log.debug("'{}' invoked by '{}' with authorities: '{}'",method,username,auth.getAuthorities().stream()
        .map(String::valueOf)
        .collect(Collectors.joining(",")));
        }
```

