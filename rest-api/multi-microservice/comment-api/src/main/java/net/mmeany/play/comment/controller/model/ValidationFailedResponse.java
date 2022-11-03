package net.mmeany.play.comment.controller.model;

import java.util.List;

public record ValidationFailedResponse(
        String message,
        List<ValidationMessage> errors
) {
}

