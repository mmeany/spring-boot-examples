package net.mmeany.play.comment.controller.model;

import net.mmeany.play.comment.model.CommentRequest;

public record CommentRequestImpl(
        Long blogId,
        Long inReplyToCommentId,
        String title,
        String content
) implements CommentRequest {}
