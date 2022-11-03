package net.mmeany.play.springenvers.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.mmeany.play.springenvers.controller.model.NoteDto;
import net.mmeany.play.springenvers.controller.model.NoteRequestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static net.mmeany.play.springenvers.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test basic authentication is working and that all /note endoints perform as expected.
 */

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles(profiles = {"test", "test-containers"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteControllerTestContainersIT {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldFailBasicAuthWithNoCredentials() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailBasicAuthWithBadCredentials() throws Exception {
        mockMvc.perform(get("/note")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotFailBasicAuthWithGoodCredentials() throws Exception {
        mockMvc.perform(get("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateAndFetch() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        JSONArray values = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.*");
        assertThat(values.isEmpty(), is(true));

        NoteRequestImpl request = noteRequest();
        mvcResult = mockMvc.perform(post("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andReturn();

        NoteDto noteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), NoteDto.class);
        assertNoteDto(noteDto);

        mvcResult = mockMvc.perform(get("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        List<NoteDto> notes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(notes.size(), is(1));
        assertNoteDto(notes.get(0));

        mvcResult = mockMvc.perform(get("/note/{id}", noteDto.id())
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();
        noteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), NoteDto.class);
        assertNoteDto(noteDto);
    }
}