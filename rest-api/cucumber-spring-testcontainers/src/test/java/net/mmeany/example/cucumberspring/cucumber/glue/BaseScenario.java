package net.mmeany.example.cucumberspring.cucumber.glue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.mmeany.example.cucumberspring.config.ApplicationConfiguration;
import net.mmeany.example.cucumberspring.controller.model.GreetingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static net.mmeany.example.cucumberspring.cucumber.glue.SpringBootTestContainersLoader.KEYCLOAK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class BaseScenario {

    @Autowired
    protected CucumberContextHolder ctx;

    @Autowired
    protected ApplicationConfiguration cfg;

    @Autowired
    protected ObjectMapper mapper;

    @LocalServerPort
    protected Integer port;

    protected Credentials getCredentials(String user) {
        Credentials credentials = null;
        switch (user) {
            case "John": {
                credentials = new Credentials("test-user-1", "Password123");
                break;
            }
            case "Paula": {
                credentials = new Credentials("test-user-2", "Password123");
                break;
            }
            case "Steve": {
                credentials = new Credentials("test-user-3", "Password123");
                break;
            }
            case "GUEST": {
                credentials = new Credentials("test-user-4", "Password123");
                break;
            }
            default: {credentials = new Credentials(null, null);}
        }
        return credentials;
    }

    protected String getUrl(String path) {
        return "http://localhost:" + port + path;
    }

    // Utility methods to handle talking to the API and Keycloak

    protected <T> ApiResponse<T> postToApi(String endpoint, Credentials credentials, Map<String, String> params, Object body, Class<T> clazz, HttpStatus expected) {

        log.info("Getting token for '{}', '{}'", credentials.username(), credentials.password());

        String token = credentials.username() == null
                ? null
                : getToken(credentials.username(), credentials.password());

        log.info("Got token: '{}'", token);
        RequestSpecification request = (token == null)
                ? given()
                : given().auth().oauth2(token).request();

        params.forEach(request::param);

        request.body(body).accept(ContentType.JSON).contentType(ContentType.JSON);

        request.log().all();

        Response response = (token == null)
                ? request.when().post(getUrl(endpoint))
                : request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token).when().post(getUrl(endpoint));

        try {
            return response.getStatusCode() == expected.value()
                    ? ApiResponse.<T>builder()
                    .status(HttpStatus.valueOf(response.getStatusCode()))
                    .response(mapper.readValue(response.getBody().asString(), clazz))
                    .build()
                    : ApiResponse.<T>builder()
                    .errored(true)
                    .status(HttpStatus.valueOf(response.getStatusCode()))
                    .errorBody(response.getBody().asString())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<T>builder()
                    .errored(true)
                    .errorMessage(e.getMessage())
                    .status(HttpStatus.valueOf(response.getStatusCode()))
                    .build();
        }
    }


    protected String aPostRequestToApplication(String username, String password) {
        GreetingRequest request = GreetingRequest.builder()
                .name(username)
                .build();

        given()
                .auth().preemptive().basic(username, password)
                .body(request).contentType(ContentType.JSON).accept(ContentType.JSON)
                .log().all()
                .when().post(getUrl("/hello"))
                .then().statusCode(200);
        return "";
    }

    /**
     * Get an access token (JWT) from TestContainers Keycloak instance.
     *
     * @param username user credential
     * @param password user credential
     * @return the JWT access token
     */
    protected String getToken(String username, String password) {
        String url = KEYCLOAK.getAuthServerUrl() + "realms/dev/protocol/openid-connect/token";
        Response response = given()
                .formParam("client_id", cfg.getKeycloak().getClientId())
                .formParam("grant_type", "password")
                .formParam("client_secret", cfg.getKeycloak().getClientSecret())
                .formParam("scope", "openid")
                .formParam("username", username)
                .formParam("password", password)
                .log().all()
                .when().post(url);

        return respond(response, KeycloakTokenResponse.class, HttpStatus.OK).accessToken;
    }

    protected <T> T postFormToApi(String endpoint, String username, String password, Map<String, String> params, Map<String, String> form, Class<T> clazz, HttpStatus expected) throws Exception {
        String token = getToken(username, password);
        RequestSpecification request = given().auth().oauth2(token);
        params.forEach(request::param);
        form.forEach(request::formParam);
        Response response = request.log().all().when().post(getUrl(endpoint));

        return respond(response, clazz, expected);
    }

    protected <T> T postObjectToApi(String endpoint, String username, String password, Map<String, String> params, Object content, Class<T> clazz, HttpStatus expected) throws Exception {
        String token = getToken(username, password);
        RequestSpecification request = given().auth().oauth2(token);
        params.forEach(request::param);
        request.body(content).contentType(ContentType.JSON).accept(ContentType.JSON);
        Response response = request.log().all().when().post(getUrl(endpoint));

        return respond(response, clazz, expected);
    }

    private <T> T respond(Response response, Class<T> clazz, HttpStatus expected) {
        assertThat(response.getStatusCode(), is(expected.value()));
        log.debug(response.getBody().asString());

        T object = null;
        try {
            object = mapper.readValue(response.getBody().asString(), clazz);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        return object;
    }

    public record KeycloakTokenResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("expires_in")
            Integer expiresIn,
            @JsonProperty("refresh_expires_in")
            Integer refreshExpiresIn,
            @JsonProperty("refresh_token")
            String refreshToken,
            @JsonProperty("token_type")
            String tokenType,
            @JsonProperty("id_token")
            String idToken,
            @JsonProperty("not-before-policy")
            Integer notBeforePolicy,
            @JsonProperty("session_state")
            String sessionState,
            @JsonProperty("scope")
            String scope
    ) {}

    public record Credentials(
            String username,
            String password
    ) {}
}
