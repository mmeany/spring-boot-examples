package net.mmeany.play.comment.model;

/**
 * All information required to create a new blog post comment.
 */
public interface CommentRequest {
    /**
     * Identifier of the blog that this comment relates to.
     *
     * @return parent blog identifier
     */
    Long blogId();

    /**
     * If this comment is a reply to an existing comment on the blog post, then this is the
     * identifier of that comment.
     * <p>
     * Can be null in which case it is a direct comment on the blog post.
     *
     * @return optional identifier of comment that this comment is replying to
     */
    Long inReplyToCommentId();

    /**
     * Title of this comment.
     *
     * @return comment title
     */
    String title();

    /**
     * Content of this comment
     *
     * @return comment content
     */
    String content();
}
