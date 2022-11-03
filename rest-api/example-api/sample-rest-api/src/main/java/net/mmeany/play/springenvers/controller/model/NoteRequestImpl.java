package net.mmeany.play.springenvers.controller.model;

import io.swagger.v3.oas.annotations.Parameter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record NoteRequestImpl (

        @Parameter(description = "The notes title")
        @NotBlank
        @Size(min = 3, message = "Must be at least 3 characters")
        String title,

        @Parameter(description = "The notes content")
        @NotBlank
        @Size(min = 3, message = "Must be at least 3 characters")
        String description
)  implements NoteRequest {}
