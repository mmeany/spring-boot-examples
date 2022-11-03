package net.mmeany.play.quote.controller;

import com.jayway.jsonpath.JsonPath;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.json.JacksonJsonParser;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AbstractTestContainersBase {

    protected final CloseableHttpClient client = buildHttpClient();

    private String keycloakPublicKey;
    private final Map<String, String> keycloakClientSecrets = new HashMap<>();

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:19.0.1")
            .withRealmImportFile("/dev-realm.json")
            .waitingFor(Wait.forHttp("/").forStatusCode(200));

    // This does not work, for some reason we get an error initializing the context
    //    @DynamicPropertySource
    //    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
    //        registry.add("security.keycloak.public-key", () -> keycloakPublicKey());
    //    }

    protected String keycloakUrl() {
        return keycloak.getAuthServerUrl();
    }

    protected String keycloakPublicKey() {
        if (keycloakPublicKey == null) {
            String response = httpGet(keycloak.getAuthServerUrl() + "realms/dev", null, Map.of());
            JacksonJsonParser jsonParser = new JacksonJsonParser();
            keycloakPublicKey = jsonParser.parseMap(response)
                    .get("public_key")
                    .toString();
        }
        return keycloakPublicKey;
    }

    protected String getClientSecret(String clientId) {
        if (!keycloakClientSecrets.containsKey(clientId)) {
            // JWT=`curl -d "client_id=admin-cli" -d "username=mark" -d "password=my-secret-pw" -d "grant_type=password" "http://192.168.1.145:9080/realms/master/protocol/openid-connect/token" | jq -r .access_token`
            String response = httpPost(keycloak.getAuthServerUrl() + "realms/master/protocol/openid-connect/token",
                    null,
                    Map.of(
                            "client_id", "admin-cli",
                            "username", keycloak.getAdminUsername(),
                            "password", keycloak.getAdminPassword(),
                            "grant_type", "password"
                    ));
            String apiToken = new JacksonJsonParser().parseMap(response)
                    .get("access_token")
                    .toString();
            //CLIENT_UUID=`curl -X GET -s -H "Authorization: bearer ${JWT}" "http://192.168.1.145:9080/admin/realms/dev/clients" | jq -r '.[] | select(.clientId == "blog-service") | .id'`
            response = httpGet(keycloak.getAuthServerUrl() + "admin/realms/dev/clients", apiToken, Map.of());
            List<String> result = JsonPath.read(response, "$[*][?(@.clientId == \"" + clientId + "\")].id");
            if (result == null || result.size() != 1) {
                throw new RuntimeException("Did not get a single result for client UUID for " + clientId);
            }
            String clientUUID = result.get(0);

            //CLIENT_SECRET=`curl -X GET -s -H "Authorization: bearer ${JWT}" "http://192.168.1.145:9080/admin/realms/dev/clients/${CLIENT_UUID}/client-secret" | jq -r .value`
            response = httpGet(keycloak.getAuthServerUrl() + "admin/realms/dev/clients/" + clientUUID + "/client-secret", apiToken, Map.of());
            keycloakClientSecrets.put(clientId, JsonPath.read(response, "$.value"));
        }
        return keycloakClientSecrets.get(clientId);
    }

    protected String getAccessToken(String realm, String clientId, String username, String password) {
        String clientSecret = getClientSecret(clientId);
        String response = httpPost(keycloak.getAuthServerUrl() + "realms/" + realm + "/protocol/openid-connect/token", null, Map.of(
                "client_id", clientId,
                "grant_type", "password",
                "client_secret", clientSecret,
                "scope", "openid",
                "username", username,
                "password", password
        ));
        return JsonPath.read(response, "$.access_token");
    }

    private static CloseableHttpClient buildHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().build();
        return HttpClientBuilder.create()
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    protected String httpGet(String url, String accessToken, Map<String, String> params) {
        return httpGet(url, accessToken, params, HttpStatus.SC_OK);
    }

    protected String httpGet(String url, String accessToken, Map<String, String> params, int expectedReturnCode) {
        URI endpointUri;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            params.forEach(uriBuilder::addParameter);
            endpointUri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpGet httpGet = new HttpGet(endpointUri);
        if (accessToken != null) {
            httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != expectedReturnCode) {
                String msg = String.format("something went wrong calling %s, status=%s", endpointUri, response.getStatusLine());
                throw new IOException(msg);
            }
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected String httpPost(String url, String accessToken, Map<String, String> fields) {
        return httpPost(url, accessToken, fields, HttpStatus.SC_OK);
    }

    protected String httpPost(String url, String accessToken, Map<String, String> fields, int expectedReturnCode) {
        URI endpointUri = null;
        try {
            endpointUri = new URIBuilder(url).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        List<NameValuePair> form = new ArrayList<>();
        fields.forEach((k, v) -> form.add(new BasicNameValuePair(k, v)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

        HttpPost httpPost = new HttpPost(endpointUri);
        if (accessToken != null) {
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = client.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != expectedReturnCode) {
                String msg = String.format("something went wrong calling %s, status=%s", endpointUri, response.getStatusLine());
                throw new RuntimeException(msg);
            }
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
