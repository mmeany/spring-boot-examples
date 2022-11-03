package net.mmeany.play.blog.controller.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Builder;
import lombok.Value;
import net.mmeany.play.blog.model.BlogSearchFilter;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Value
@Builder
public class SearchFilter implements BlogSearchFilter {
    @Parameter(
            description = "Restrict to blog posts with this phrase in the title"
    )
    @Size(max = 200)
    String title;

    @Parameter(
            description = "Restrict to blog posts with this phrase in the description"
    )
    @Size(max = 200)
    String content;

    @Parameter(
            description = "Restrict to blog posts created by this user"
    )
    @Size(max = 200)
    String createdBy;

    @Parameter(
            description = "Restrict to blog posts created on or after this date"
    )
    @Past
    Date createdAfter;

    @Parameter(
            description = "Restrict to blog posts created on or before this date"
    )
    @Past
    Date createdBefore;

    @Parameter(
            description = "Restrict to blog posts tagged with all these tags"
    )
    Set<String> tags;

    public boolean hasFilters() {
        return getTitle() != null ||
                getContent() != null ||
                getCreatedBy() != null ||
                getCreatedAfter() != null ||
                getCreatedBefore() != null ||
                getTags() != null
                ;
    }
}
