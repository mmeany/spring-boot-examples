package net.mmeany.play.blog.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Set;

@SuppressFBWarnings("BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS")
@Entity
@Table(name = "blog")
@Audited
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Blog extends BaseEntity {

    @Column(nullable = false, length = 400)
    private String title;

    @Lob
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "blog_tags",
            joinColumns = {
                    @JoinColumn(name = "blog_id", referencedColumnName = "id")
            }
    )
    @Column(name = "tag")
    private Set<String> tags;
}
