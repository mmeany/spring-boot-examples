package net.mmeany.example.controller.model;

public record ValidationMessage(
        String field,
        String message
) {}
