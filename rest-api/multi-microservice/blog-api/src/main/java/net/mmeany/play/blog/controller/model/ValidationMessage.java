package net.mmeany.play.blog.controller.model;

public record ValidationMessage(
        String field,
        String message
) {}
