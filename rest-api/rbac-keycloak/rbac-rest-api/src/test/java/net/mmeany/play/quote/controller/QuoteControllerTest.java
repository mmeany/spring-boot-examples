package net.mmeany.play.quote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mmeany.play.quote.TestData;
import net.mmeany.play.quote.config.ApplicationConfiguration;
import net.mmeany.play.quote.config.SecurityConfiguration;
import net.mmeany.play.quote.controller.model.QuoteDto;
import net.mmeany.play.quote.controller.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static net.mmeany.play.quote.TestData.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuoteController.class)
@ExtendWith(SpringExtension.class)
@Import({ApplicationConfiguration.class, SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class QuoteControllerTest {

    @MockBean
    private QuoteService quoteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldFailWithNoCredentials() throws Exception {
        mockMvc.perform(get("/quote"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailBasicAuthWithBadCredentials() throws Exception {
        mockMvc.perform(get("/quote")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSucceedWithAdminBasicAuthCredentials() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);
        MvcResult mvcResult = mockMvc.perform(get("/quote")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }

    @Test
    void shouldSucceedWithManagerBasicAuthCredentials() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);
        MvcResult mvcResult = mockMvc.perform(get("/quote")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }

    @Test
    void shouldSucceedWithNoRolesBasicAuthCredentials() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);
        MvcResult mvcResult = mockMvc.perform(get("/quote")
                        .with(httpBasic(NO_ROLES_USERNAME, NO_ROLES_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }

    @Test
    void shouldSucceedWithMemberBasicAuthCredentials() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);
        MvcResult mvcResult = mockMvc.perform(get("/quote")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }

    @Test
    void cannotViewMemberQuotesWithoutMemberRole() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);

        mockMvc.perform(get("/quote/member")
                        .with(httpBasic(NO_ROLES_USERNAME, NO_ROLES_PASSWORD))
                )
                .andExpect(status().isForbidden());

        MvcResult mvcResult = mockMvc.perform(get("/quote/member")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);

        mvcResult = mockMvc.perform(get("/quote/member")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);

        mvcResult = mockMvc.perform(get("/quote/member")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }

    @Test
    void cannotViewManagerQuotesWithoutManagerRole() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);

        mockMvc.perform(get("/quote/manager")
                        .with(httpBasic(NO_ROLES_USERNAME, NO_ROLES_PASSWORD))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/quote/manager")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isForbidden());

        MvcResult mvcResult = mockMvc.perform(get("/quote/manager")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);

        mvcResult = mockMvc.perform(get("/quote/manager")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }

    @Test
    void cannotViewAdminQuotesWithoutAdminRole() throws Exception {
        QuoteDto quoteDto = TestData.quoteDto();
        when(quoteService.nextQuote()).thenReturn(quoteDto);

        mockMvc.perform(get("/quote/admin")
                        .with(httpBasic(NO_ROLES_USERNAME, NO_ROLES_PASSWORD))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/quote/admin")
                        .with(httpBasic(MEMBER_USERNAME, MEMBER_PASSWORD))
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/quote/admin")
                        .with(httpBasic(MANAGER_USERNAME, MANAGER_PASSWORD))
                )
                .andExpect(status().isForbidden());

        MvcResult mvcResult = mockMvc.perform(get("/quote/admin")
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        QuoteDto returnedQuoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuoteDto.class);
        assertQuoteDto(returnedQuoteDto);
    }
}
