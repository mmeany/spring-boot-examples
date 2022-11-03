package net.mmeany.play.quote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mmeany.play.quote.controller.model.QuoteDto;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import static net.mmeany.play.quote.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test basic authentication is working and that all /quote endpoints perform as expected.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles(profiles = {"test", "test-containers"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuoteControllerOAuthTestContainersIT extends AbstractTestContainersBase {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void lookAtKeycloak() throws Exception {
        assertThat(keycloakPublicKey(), notNullValue());
    }

    @Test
    void lookAtKeycloakAgain() throws Exception {
        assertThat(getClientSecret("blog-service"), notNullValue());
    }

    @Test
    void getAnAccessTokenFromKeycloak() throws Exception {
        assertThat(getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD), notNullValue());
    }

    @Test
    void shouldNotFailAuthWithAdminCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        mockMvc.perform(get("/quote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotFailAuthWithManagerCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MANAGER_USERNAME, MANAGER_PASSWORD);
        mockMvc.perform(get("/quote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotFailAuthWithMemberCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/quote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotFailAuthWithNoRolesCredentials() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/quote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFetch() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        MvcResult mvcResult = mockMvc.perform(get("/quote")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andReturn();
        QuoteDto quoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertThat(quoteDto, notNullValue());
        assertThat(quoteDto.quote(), notNullValue());
    }

    @Test
    void cannotViewManagerQuotesWithoutManagerRole() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, NO_ROLES_USERNAME, NO_ROLES_PASSWORD);
        mockMvc.perform(get("/quote/manager")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());

        accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/quote/manager")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());

        accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MANAGER_USERNAME, MANAGER_PASSWORD);
        MvcResult mvcResult = mockMvc.perform(get("/quote/manager")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andReturn();
        QuoteDto quoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertThat(quoteDto, notNullValue());
        assertThat(quoteDto.quote(), notNullValue());

        accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        mvcResult = mockMvc.perform(get("/quote/manager")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andReturn();
        quoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertThat(quoteDto, notNullValue());
        assertThat(quoteDto.quote(), notNullValue());
    }

    @Test
    void cannotViewAdminQuotesWithoutAdminRole() throws Exception {
        String accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, NO_ROLES_USERNAME, NO_ROLES_PASSWORD);
        mockMvc.perform(get("/quote/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());

        getAccessToken(TEST_REALM, TEST_CLIENT_ID, MEMBER_USERNAME, MEMBER_PASSWORD);
        mockMvc.perform(get("/quote/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());

        accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, MANAGER_USERNAME, MANAGER_PASSWORD);
        mockMvc.perform(get("/quote/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());

        accessToken = getAccessToken(TEST_REALM, TEST_CLIENT_ID, ADMIN_USERNAME, ADMIN_PASSWORD);
        MvcResult mvcResult = mockMvc.perform(get("/quote/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andReturn();
        QuoteDto quoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertThat(quoteDto, notNullValue());
        assertThat(quoteDto.quote(), notNullValue());
    }
}