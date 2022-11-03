package net.mmeany.play.springenvers.controller.model;

public record ValidationMessage(
        String field,
        String message
) {}
