package net.mmeany.example.common.config.controller.model;

import java.util.List;

public record ValidationFailedResponse(
        String message,
        List<ValidationMessage> errors
) {
}

