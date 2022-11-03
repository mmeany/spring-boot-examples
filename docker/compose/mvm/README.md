# Introduction

The `docker-compose.yml` file in this directory will launch a number of containers used in various projects in the repository.

These have been configured to retain data on restarts using `volumes`.

The restart policy has been set to `unless-stopped` allowing individual containers to be stopped if they are too heavyweight (Pulsar for example).

To launch the containers then, open a shell in this directory (`docker/compose/mvm`) and issue the following command:

```shell
docker compose up -d
```

# Containers and ports they expose

| Service    | Name         | Ports            | Notes                          |
|------------|--------------|------------------|--------------------------------|
| Keycloak   | mvm-keycloak | 9080             | Authentication server          |
| PostgreSQL | mvm-postgres | 5432             | Database with PostGIS          |
| Pulsar     | mvm-pulsar   | 6650, 9091       | Messaging server               |
| Inbucket   | mvm-inbucket | 9092, 2500, 1100 | Mail server with web interface |
| SFTP       | mvm-sftp     | 9093             | FTP & SFTP server              |
| Mysql      | mvm-mysql8   | 3306             | Database                       |

## Keycloak

The `dev` realm configuration is loaded on startup.

Useful URLs

* [Keycloak](http://127.0.0.1:9080), username `mark`, password `my-secret-pw`
* [Keycloak Public Key](http://127.0.0.1:9080/realms/dev)
* [Keycloak JWKS](http://127.0.0.1:9080/realms/dev/.well-known/openid-configuration)

The `dev` realm is pre-configured with a few `client`s used in various projects. There are three users that have various `roles` granted to them (and thus exposed in the `JWT`).

| Username    | Password    | Notes                                    |
|-------------|-------------|------------------------------------------|
| test-user-1 | Password123 | An ADMIN user for all services           |
| test-user-2 | Password123 | A user with CREATE role for each service |
| test-user-3 | Password123 | A user with no specific roles            |

An example `curl` command to obtain a `JWT` for one of these users:

```shell
curl -L -X POST 'http://127.0.0.1:9080/realms/dev/protocol/openid-connect/token' \
 -H 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'client_id=blog-service' \
 --data-urlencode 'grant_type=password' \
 --data-urlencode 'client_secret=bRXguGMmWUCFKWJcMJeJgnNbuQBZVT2u' \
 --data-urlencode 'scope=openid' \
 --data-urlencode 'username=test-user-1' \
 --data-urlencode 'password=Password123' | jq .
```

_NOTE_: When calling `Keycloak` to obtain a `JWT` (from Postman for example), do not call `localhost`, use the IP address `127.0.0.1` or some resolvable domain. If you use `localhost` chances
are you will hit an authentication error along the lines of:

```text
Response Headers
	WWW-Authenticate: Bearer realm="dev", error="invalid_token", error_description="Invalid token issuer. Expected 'http://127.0.0.1:9080/realms/dev', but was 'http://localhost:9080/realms/dev'"
```

### Keycloak Admin API

Documentation for the Admin API can be found [here](https://www.keycloak.org/docs-api/19.0.3/rest-api/index.html#_overview)

Some sample `curl` commands should suffice:

```shell
JWT=`curl \
	-d "client_id=admin-cli" \
	-d "username=mark" \
	-d "password=my-secret-pw" \
	-d "grant_type=password" \
	"http://192.168.1.145:9080/realms/master/protocol/openid-connect/token" | jq -r .access_token`  
CLIENT_UUID=`curl -X GET -s \
	-H "Authorization: bearer ${JWT}" \
	"http://192.168.1.145:9080/admin/realms/dev/clients" | jq -r '.[] | select(.clientId == "blog-service") | .id'`
CLIENT_SECRET=`curl -X GET -s \
	-H "Authorization: bearer ${JWT}" \
	"http://192.168.1.145:9080/admin/realms/dev/clients/${CLIENT_UUID}/client-secret" | jq -r .value`
echo "${CLIENT_SECRET}"
```

_NOTE_: The admin `JWT` has a very short timeout, 60 seconds.

## Postgres

Postgres will be available on port `5432`, admin username and password are `mark` and `my-secret-pw`.

All services use the same database at the moment, `mvm`, and the `public` schema.

