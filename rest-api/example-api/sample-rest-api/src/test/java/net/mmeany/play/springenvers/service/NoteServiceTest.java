package net.mmeany.play.springenvers.service;

import net.mmeany.play.springenvers.controller.model.NoteDto;
import net.mmeany.play.springenvers.model.Note;
import net.mmeany.play.springenvers.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static net.mmeany.play.springenvers.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Captor
    private ArgumentCaptor<Note> noteCaptor;

    @Test
    void create() {
        Note note = Note.builder()
                .id(1L)
                .created(DATE)
                .createdBy(TEST_USER_1_USERNAME)
                .title(TITLE)
                .description(DESCRIPTION)
                .build();
        when(noteRepository.save(any())).thenReturn(note);

        NoteDto savedNoteDto = noteService.create(TITLE, DESCRIPTION);
        verify(noteRepository, times(1)).save(noteCaptor.capture());

        Note persistedNote = noteCaptor.getValue();
        assertThat(persistedNote, notNullValue());
        assertAll(
                () -> assertThat(persistedNote.getId(), nullValue()),
                () -> assertThat(persistedNote.getCreated(), nullValue()),
                () -> assertThat(persistedNote.getCreatedBy(), nullValue()),
                () -> assertThat(persistedNote.getTitle(), is(TITLE)),
                () -> assertThat(persistedNote.getDescription(), is(DESCRIPTION))
        );

        assertAll(
                () -> assertThat(savedNoteDto.id(), is(note.getId())),
                () -> assertThat(savedNoteDto.created(), is(note.getCreated())),
                () -> assertThat(savedNoteDto.createdBy(), is(note.getCreatedBy())),
                () -> assertThat(savedNoteDto.title(), is(note.getTitle())),
                () -> assertThat(savedNoteDto.description(), is(note.getDescription()))
        );
    }

    @Test
    void notes() {
        NoteDto noteDto = noteDto();
        List<NoteDto> notes = List.of(noteDto);
        when(noteRepository.findAllAsDtoBy()).thenReturn(notes);

        List<NoteDto> returnedNotes = noteService.notes();
        verify(noteRepository, times(1)).findAllAsDtoBy();

        assertThat(returnedNotes, notNullValue());
        assertThat(returnedNotes.size(), is(1));
        assertThat(returnedNotes.get(0), is(noteDto));
    }

    @Test
    void note() {
        NoteDto noteDto = noteDto();
        when(noteRepository.findAsDtoById(ID)).thenReturn(Optional.of(noteDto));

        Optional<NoteDto> returned = noteService.note(ID);
        assertThat(returned.orElse(null), is(noteDto));
    }
}