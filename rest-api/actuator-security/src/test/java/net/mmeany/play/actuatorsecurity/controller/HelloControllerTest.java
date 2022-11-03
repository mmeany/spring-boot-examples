package net.mmeany.play.actuatorsecurity.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static net.mmeany.play.actuatorsecurity.TestData.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {"test", "test-containers"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFailWithNoCredentials() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailWithBadCredentials() throws Exception {
        mockMvc.perform(get("/hello")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSucceedWithAdminCredentials() throws Exception {
        mockMvc.perform(get("/hello")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedWithManagerCredentials() throws Exception {
        mockMvc.perform(get("/hello")
                        .with(httpBasic(TEST_USER_2_USERNAME, TEST_USER_2_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldSucceedWithMemberCredentials() throws Exception {
        mockMvc.perform(get("/hello")
                        .with(httpBasic(TEST_USER_3_USERNAME, TEST_USER_3_PASSWORD))
                )
                .andExpect(status().isOk());
    }
}