package net.mmeany.play.springenvers.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static net.mmeany.play.springenvers.TestData.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test basic authentication is working with actuator
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {"test", "test-containers"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActuatorTestContainerTest {

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
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithCreatorCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic(TEST_USER_2_USERNAME, TEST_USER_2_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedHealthEndpointWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic(TEST_USER_3_USERNAME, TEST_USER_3_PASSWORD))
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
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailInfoEndpointWithCreatorCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(TEST_USER_2_USERNAME, TEST_USER_2_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailInfoEndpointWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .with(httpBasic(TEST_USER_3_USERNAME, TEST_USER_3_PASSWORD))
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
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailMetricsEndpointWithCreatorCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(TEST_USER_2_USERNAME, TEST_USER_2_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFailMetricsEndpointWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic(TEST_USER_3_USERNAME, TEST_USER_3_PASSWORD))
                )
                .andExpect(status().isForbidden());
    }
}