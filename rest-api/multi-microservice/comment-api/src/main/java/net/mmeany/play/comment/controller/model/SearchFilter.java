package net.mmeany.play.comment.controller.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Builder;
import lombok.Value;
import net.mmeany.play.comment.model.CommentSearchFilter;

import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Value
@Builder
public class SearchFilter implements CommentSearchFilter {
    @Parameter(
            description = "Restrict to comments of this blog"
    )
    @Positive
    Long blogId;

    @Parameter(
            description = "Restrict to comments made in reply to this comment"
    )
    @Positive
    Long inReplyToCommentId;

    @Parameter(
            description = "Restrict to comments created by this user"
    )
    @Size(max = 200)
    String createdBy;

    @Parameter(
            description = "Restrict to comments created on or after this date"
    )
    @Past
    Date createdAfter;

    @Parameter(
            description = "Restrict to comments created on or before this date"
    )
    @Past
    Date createdBefore;

    public boolean hasFilters() {
        return getBlogId() != null ||
                getInReplyToCommentId() != null ||
                getCreatedBy() != null ||
                getCreatedAfter() != null ||
                getCreatedBefore() != null
                ;
    }
}
