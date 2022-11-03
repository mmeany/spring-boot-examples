package net.mmeany.play.blog.controller.model;

import net.mmeany.play.blog.model.BlogRequest;

import java.util.Set;

public record BlogRequestImpl(
        String title,
        String content,
        Set<String> tags
) implements BlogRequest {}
