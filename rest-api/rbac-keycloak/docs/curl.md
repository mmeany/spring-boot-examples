# Introduction

A bunch of `curl` commands for interacting with Keycloak, the Actuator endpoint and the Quote API itself.
Make sure that Docker is running the services and launch the API from within the API.

```shell
#export KEYCLOAK_URL=http://127.0.0.1:9080
export KEYCLOAK_URL=http://192.168.1.145:9080
export CLIENT_ID=blog-service
export REALM=dev
#export API_BASE_URL=http://127.0.0.1:9081
export API_BASE_URL=http://192.168.1.145:9081
export BASIC_AUTH_USERNAME=mark
export BASIC_AUTH_PASSWORD=Password123
export JWT_USERNAME=test-user-1
export JWT_PASSWORD=Password123

##########
########## Keycloak
##########

# Obtain client secret from Keycloak
ADMIN_JWT=`curl -s -d "client_id=admin-cli" -d "username=mark" -d "password=my-secret-pw" -d "grant_type=password" "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" | jq -r .access_token`
CLIENT_UUID=`curl -X GET -s -H "Authorization: Bearer ${ADMIN_JWT}" "${KEYCLOAK_URL}/admin/realms/dev/clients" | jq -r '.[] | select(.clientId == "blog-service") | .id'`
export CLIENT_SECRET=`curl -X GET -s -H "Authorization: Bearer ${ADMIN_JWT}" "${KEYCLOAK_URL}/admin/realms/dev/clients/${CLIENT_UUID}/client-secret" | jq -r .value`
echo "${CLIENT_SECRET}"

# Obtain access token for user test-user-1
export JWT=`curl -L -s -X POST \
 -H 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode "client_id=${CLIENT_ID}" \
 --data-urlencode 'grant_type=password' \
 --data-urlencode "client_secret=${CLIENT_SECRET}" \
 --data-urlencode 'scope=openid' \
 --data-urlencode "username=${JWT_USERNAME}" \
 --data-urlencode "password=${JWT_PASSWORD}" \
 "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token"| jq -r .access_token`
echo "${JWT}"

##########
########## Actuator
##########

# Basic Authentication
curl -X GET -s "${API_BASE_URL}/actuator/health" | jq .
curl -X GET -s -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" "${API_BASE_URL}/actuator/info" | jq .
curl -X GET -s -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" "${API_BASE_URL}/actuator/metrics" | jq .

# OAuth2
curl -X GET -s -H "Authorization: Bearer ${JWT}" "${API_BASE_URL}/actuator/info" | jq .
curl -X GET -s -H "Authorization: Bearer ${JWT}" "${API_BASE_URL}/actuator/metrics" | jq .

##########
########## Quote API
##########

# Basic Authentication
curl -X GET -s -u "${BASIC_AUTH_USERNAME}:${BASIC_AUTH_PASSWORD}" "${API_BASE_URL}/quote" | jq -r .quote

# OAuth2
curl -X GET -s -H "Authorization: bearer ${JWT}" "${API_BASE_URL}/quote" | jq -r .quote
```

