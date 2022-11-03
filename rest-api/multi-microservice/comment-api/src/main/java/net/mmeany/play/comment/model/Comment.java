package net.mmeany.play.comment.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Set;

@SuppressFBWarnings("BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS")
@Entity
@Table(name = "comment")
@Audited
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    private Long blogId;

    @OneToOne
    @JoinColumn(name = "in_reply_to_id")
    private Comment inReplyToComment;

    @OneToMany(mappedBy = "inReplyToComment", fetch = FetchType.LAZY)
    private Set<Comment> replies;

    @Column(nullable = false, length = 400)
    private String title;

    @Lob
    private String content;
}
