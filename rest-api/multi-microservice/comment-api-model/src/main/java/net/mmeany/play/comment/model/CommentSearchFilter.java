package net.mmeany.play.comment.model;

import java.util.Date;

/**
 * Filters that can be used to search for comments matching specific criteria.
 * <p>
 * All filters are additive.
 */
public interface CommentSearchFilter {
    /**
     * If set, only comments pertaining to the specified blog post will be considered.
     *
     * @return id of the bog post
     */
    Long getBlogId();

    /**
     * If set, only comments that are direct replies to the one specified are returned.
     *
     * @return id of the comment for which replies are required
     */
    Long getInReplyToCommentId();

    /**
     * If set, only comments by the specified author are returned
     *
     * @return author
     */
    String getCreatedBy();

    /**
     * If set, only comments created on or after the specified date are returned. The date is inclusive, to
     * achieve this the date is manipulated to start at 00:00:00.
     *
     * @return earliest date to consider
     */
    Date getCreatedAfter();

    /**
     * If set, only comments created on or after the specified date are returned. The date is inclusive
     * manipulated to end at 23:59:59.
     *
     * @return latest date to consider
     */
    Date getCreatedBefore();
}
