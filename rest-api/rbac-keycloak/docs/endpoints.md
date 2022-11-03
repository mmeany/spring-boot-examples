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

The `dumpAuthorities()` method is only there to provide some feedback during development, looking at the logs it is possible
to see info about the current user, obtained from either the Basic Auth user or the JWT:

```text
DEBUG n.m.p.q.controller.QuoteController [dumpAuthorities:49] - 'quote()' invoked by 'test-user-1' with authorities: 'ROLE_ADMIN'
DEBUG n.m.p.q.controller.QuoteController [dumpAuthorities:49] - 'quote()' invoked by 'mark' with authorities: 'ROLE_ADMIN'
```

