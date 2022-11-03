package net.mmeany.play.springenvers.controller.model;

import java.util.List;

public record ValidationFailedResponse(
        String message,
        List<ValidationMessage> errors
) {
}

