package net.mmeany.play.blog.model;

import java.util.Date;

/**
 * Minimal representation of a blog post.
 *
 * @param id        The blog post identifier.
 * @param title     The blog post title
 * @param content   The blog post content
 * @param tags      Comma separated list of all tags applied to this blog post
 * @param createdBy Username of the person that created this blog post
 * @param created   The date that this blog post was created
 */
public record BlogDto(
        Long id,
        String title,
        String content,
        String tags,
        String createdBy,
        Date created
) {
}
