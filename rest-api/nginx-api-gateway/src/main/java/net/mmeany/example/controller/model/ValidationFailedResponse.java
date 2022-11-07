package net.mmeany.example.controller.model;

import java.util.List;

public record ValidationFailedResponse(
        String message,
        List<ValidationMessage> errors
) {
}

