package net.mmeany.example.cucumberspring.cucumber.glue;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@Testcontainers
@ActiveProfiles(profiles = {"test", "test-containers"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpringBootTestContainersLoader {

    @Container
    public static KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:19.0.1")
            .withRealmImportFile("/dev-realm.json")
            .waitingFor(Wait.forHttp("/").forStatusCode(200));

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:14.5");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        POSTGRES.start();
        KEYCLOAK.start();

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Use JWKS
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> KEYCLOAK.getAuthServerUrl() + "realms/dev/protocol/openid-connect/certs");
    }
}
