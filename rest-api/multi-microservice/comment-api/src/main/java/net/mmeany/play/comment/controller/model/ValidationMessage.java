package net.mmeany.play.comment.controller.model;

public record ValidationMessage(
        String field,
        String message
) {}
