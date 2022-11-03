package net.mmeany.play.springenvers.repository;

import net.mmeany.play.springenvers.controller.model.NoteDto;
import net.mmeany.play.springenvers.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<NoteDto> findAsDtoById(long id);

    List<NoteDto> findAllAsDtoBy();
}
