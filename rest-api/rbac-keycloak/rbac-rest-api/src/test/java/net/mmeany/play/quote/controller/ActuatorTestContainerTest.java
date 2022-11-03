package net.mmeany.play.quote.controller;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static net.mmeany.play.quote.TestData.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test basic authentication is working with actuator
 */

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles(profiles = {"test", "test-containers"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActuatorTestContainerTest extends AbstractTestContainersBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSucceedHealthEndpointWithNoCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailHealthEndpointWithBadCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSucceedHealthEndpointWithAdminCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithManagerCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithAdminOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        mockMvc.perform(get("/actuator/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithManagerOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MANAGER_USERNAME, MANAGER_PASSWORD);
        mockMvc.perform(get("/actuator/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithMemberOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/actuator/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithNoRolesOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, NO_ROLES_USERNAME, NO_ROLES_PASSWORD);
        mockMvc.perform(get("/actuator/health")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailInfoEndpointWithNoCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailInfoEndpointWithBadCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSucceedInfoEndpointWithAdminCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailInfoEndpointWithManagerCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailInfoEndpointWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailInfoEndpointWithNoRolesCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(NO_ROLES_USERNAME, NO_ROLES_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldSucceedInfoEndpointWithAdminOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        mockMvc.perform(get("/actuator/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailInfoEndpointWithManagerOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MANAGER_USERNAME, MANAGER_PASSWORD);
        mockMvc.perform(get("/actuator/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailInfoEndpointWithMemberOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/actuator/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailInfoEndpointWithNoRolesOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, NO_ROLES_USERNAME, NO_ROLES_PASSWORD);
        mockMvc.perform(get("/actuator/info")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailMetricsEndpointWithNoCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailMetricsEndpointWithBadCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSucceedMetricsEndpointWithAdminCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailMetricsEndpointWithManagerCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailMetricsEndpointWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailMetricsEndpointWithNoRolesCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(NO_ROLES_USERNAME, NO_ROLES_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldSucceedMetricsEndpointWithAdminOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        mockMvc.perform(get("/actuator/metrics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailMetricsEndpointWithManagerOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MANAGER_USERNAME, MANAGER_PASSWORD);
        mockMvc.perform(get("/actuator/metrics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailMetricsEndpointWithMemberOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/actuator/metrics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailMetricsEndpointWithNoRolesOauthCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, NO_ROLES_USERNAME, NO_ROLES_PASSWORD);
        mockMvc.perform(get("/actuator/metrics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }
}