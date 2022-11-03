package net.mmeany.play.blog.model;

import java.util.Set;

/**
 * All information required to create a new blog post.
 * <p>
 * It should be noted that the blog title should be unique!
 */
public interface BlogRequest {

    /**
     * The title of the blog post.
     *
     * @return the blog post title
     */
    String title();

    /**
     * The content of the blog post.
     *
     * @return the blog post content
     */
    String content();

    /**
     * All tags to apply to this blog post.
     *
     * @return tags applied to this blog post
     */
    Set<String> tags();
}
