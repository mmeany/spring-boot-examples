package net.mmeany.play.springenvers.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mmeany.play.springenvers.config.ApplicationConfiguration;
import net.mmeany.play.springenvers.config.SecurityConfiguration;
import net.mmeany.play.springenvers.controller.model.NoteDto;
import net.mmeany.play.springenvers.controller.model.NoteRequestImpl;
import net.mmeany.play.springenvers.service.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static net.mmeany.play.springenvers.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
@ExtendWith(SpringExtension.class)
@Import({SecurityConfiguration.class, ApplicationConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class NoteControllerTest {

    @MockBean
    private NoteService noteService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldFailWithNoCredentials() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailWithBadCredentials() throws Exception {
        mockMvc.perform(get("/note")
                        .with(httpBasic(BAD_USERNAME, BAD_PASSWORD))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnEmptyListWhenNoNotesAvailable() throws Exception {

        when(noteService.notes()).thenReturn(List.of());
        MvcResult mvcResult = mockMvc.perform(get("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        List<NoteDto> notes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(notes, notNullValue());
        assertThat(notes.isEmpty(), is(true));
    }

    @Test
    void shouldReturnListWhenNotesAvailable() throws Exception {
        NoteDto noteDto = noteDto();
        when(noteService.notes()).thenReturn(List.of(noteDto));
        MvcResult mvcResult = mockMvc.perform(get("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        List<NoteDto> notes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(notes, notNullValue());
        assertThat(notes.isEmpty(), is(false));
        assertThat(notes.size(), is(1));
        assertThat(notes.get(0), is(noteDto));
    }

    @Test
    void shouldFailValidationWhenInvalidIdProvided() throws Exception {
        mockMvc.perform(get("/note/{id}", "bad")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailValidationWhenNegativeIdProvided() throws Exception {
        mockMvc.perform(get("/note/{id}", "-1")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenValidIdProvidedButNoteDoesNotExist() throws Exception {
        when(noteService.note(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/note/{id}", "1")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSucceedWhenValidIdProvidedAndNoteExists() throws Exception {
        NoteDto noteDto = noteDto();
        when(noteService.note(1L)).thenReturn(Optional.of(noteDto));
        MvcResult mvcResult = mockMvc.perform(get("/note/{id}", "1")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        NoteDto returnedNoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), NoteDto.class);
        assertNoteDto(returnedNoteDto);
    }

    @Test
    void shouldFailValidationWhenInvalidTitleProvided() throws Exception {
        NoteRequestImpl request = badNoteRequest(null, null);
        mockMvc.perform(post("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSucceedWhenValid() throws Exception {
        NoteRequestImpl request = noteRequest();
        NoteDto noteDto = noteDto();
        when(noteService.create(request.title(), request.description())).thenReturn(noteDto);
        MvcResult mvcResult = mockMvc.perform(post("/note")
                        .with(httpBasic(TEST_USER_1_USERNAME, TEST_USER_1_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andReturn();
        NoteDto savedNoteDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), NoteDto.class);
        assertNoteDto(savedNoteDto);
    }
}
