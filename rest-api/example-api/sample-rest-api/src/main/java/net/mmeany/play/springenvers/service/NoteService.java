package net.mmeany.play.springenvers.service;

import lombok.extern.slf4j.Slf4j;
import net.mmeany.play.springenvers.controller.model.NoteDto;
import net.mmeany.play.springenvers.model.Note;
import net.mmeany.play.springenvers.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional()
    public NoteDto create(String title, String description) {
        Note note = noteRepository.save(Note.builder().title(title).description(description).build());
        return noteToDto(note);
    }

    public List<NoteDto> notes() {
        return noteRepository.findAllAsDtoBy();
    }

    public Optional<NoteDto> note(Long id) {
        return noteRepository.findAsDtoById(id);
    }

    private NoteDto noteToDto(Note note) {
        return new NoteDto(
                note.getId(),
                note.getCreated(),
                note.getCreatedBy(),
                note.getTitle(),
                note.getDescription()
        );
    }
}
