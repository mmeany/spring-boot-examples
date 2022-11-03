package net.mmeany.play.blog.model;

import java.util.Date;
import java.util.Set;

/**
 * Filters that can be used to search for blog posts matching specific criteria.
 * <p>
 * All filters are additive.
 */
public interface BlogSearchFilter {

    /**
     * If set, only blog posts containing this phrase in the title will be returned.
     *
     * @return id of the bog post
     */
    String getTitle();

    /**
     * If set, only blog posts containing this phrase in the content will be returned.
     *
     * @return id of the bog post
     */
    String getContent();

    /**
     * If set, only blog posts by the specified author are returned
     *
     * @return author
     */
    String getCreatedBy();

    /**
     * If set, only blog posts created on or after the specified date are returned. The date is inclusive, to
     * achieve this the date is manipulated to start at 00:00:00.
     *
     * @return earliest date to consider
     */
    Date getCreatedAfter();

    /**
     * If set, only blog posts created on or after the specified date are returned. The date is inclusive
     * manipulated to end at 23:59:59.
     *
     * @return latest date to consider
     */
    Date getCreatedBefore();

    /**
     * If set, only blog posts tagged with these tags are returned.
     *
     * @return latest date to consider
     */
    Set<String> getTags();
}
