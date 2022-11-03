package net.mmeany.play.springenvers.controller.model;

import java.util.Date;

public record NoteDto(
        Long id,
        Date created,
        String createdBy,
        String title,
        String description
) {}
