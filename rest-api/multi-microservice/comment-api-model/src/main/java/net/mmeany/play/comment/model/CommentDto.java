package net.mmeany.play.comment.model;

import java.util.Date;

/**
 * Minimal representation of a blog comment
 *
 * @param id                 The comment identifier
 * @param blogId             The blog post this comment is associated with
 * @param inReplyToCommentId The comment that this comment is replying to
 * @param title              The title of this comment
 * @param content            The content of this comment
 * @param createdBy          Username of the user that created this comment
 * @param created            Date that this comment was created
 */
public record CommentDto(
        Long id,
        Long blogId,
        Long inReplyToCommentId,
        String title,
        String content,
        String createdBy,
        Date created
) {
}
