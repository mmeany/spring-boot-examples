# Getting started

To access these microservices requires a valid `JWT` which can be obtained from `Keycloak`. They also require a `Postgres` database.
To facilitate this there is a Docker Compose configuration that will start up both of these services locally. From a terminal with current
working directory of the `docker` directory in this project:

```shell
docker compose up -d
```

## Postgres instance

Postgres will be available on port `5432`, admin username and password are `mark` and `my-secret-pw`. All services use the same database at the moment, `mvm`, and the `public` schema.

## Keycloak instance

Keycloak will be available on port [9080](http://127.0.0.1:8080/), admin username and password are `mark` and `my-secret-pw`. The realm used for this is `dev`.

The public key for this realm can be obtained from [http://127.0.0.1:9080/realms/dev](http://127.0.0.1:9080/realms/dev).

The `dev` realm is pre-configured with a `client` for each microservice and three users that have various `roles` granted to them (and thus exposed in the `JWT`).

| Username    | Password    | Notes                                    |
|-------------|-------------|------------------------------------------|
| test-user-1 | Password123 | An ADMIN user for all services           |
| test-user-2 | Password123 | A user with CREATE role for each service |
| test-user-3 | Password123 | A user with no specific roles            |

_NOTE_: When calling `Keycloak` to obtain a `JWT` (from Postman for example), do not call `localhost`, use the IP address `127.0.0.1` or some resolvable domain. If you use `localhost` chances
are you will hit an authentication error along the lines of:

```text
Response Headers
	WWW-Authenticate: Bearer realm="dev", error="invalid_token", error_description="Invalid token issuer. Expected 'http://127.0.0.1:9080/realms/dev', but was 'http://localhost:9080/realms/dev'"
```

---

# Other notes

## Security configuration

I wanted to allow both Basic Authentication and Keycloak provided JWT authentication for all services. The Keycloak adapter struggles with most recent Spring Security (at time of creation) and I was
unable to get both authentication schemes playing nicely using the adapter. There is instead a totally separate security configuration for using both schemes together, this was an interesting exercise
as it demonstrates manually creating JWT files and extraction of roles from Keycloak configured client scopes.

In `application.yml` there is a configuration value that allows the type of authentication to be selected. Configuration is selected accordingly using annotation `@ConditionalOnProperty` on the
`@Configuration` classes.

## JSON columns

Tried storing `tags` in a JSON column using `hibernate-types`, but this did not play well with `QueryDSL`. Had to revert to `@Embeddable` and now hitting the lots of queries issue when fetching all
`tags`. This could be averted using a `JQuery` and not the repo, maybe later.
