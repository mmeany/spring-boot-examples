package net.mmeany.play.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import net.mmeany.play.comment.controller.model.SearchFilter;
import net.mmeany.play.comment.model.Comment;
import net.mmeany.play.comment.model.CommentDto;
import net.mmeany.play.comment.model.QComment;
import net.mmeany.play.comment.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentDto create(Long blogId, Long inReplyToCommentId, String title, String content) {
        Comment inReplyToComment = Optional.ofNullable(inReplyToCommentId)
                .map(commentRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);

        return commentToDto(commentRepository.save(Comment.builder()
                .blogId(blogId)
                .inReplyToComment(inReplyToComment)
                .title(title)
                .content(content)
                .build()));
    }

    public Page<CommentDto> comments(SearchFilter f, Pageable pageable) {
        if (f.hasFilters()) {
            Date startDate = Optional.ofNullable(f.getCreatedAfter()).map(this::startDate).orElse(null);
            Date endDate = Optional.ofNullable(f.getCreatedBefore()).map(this::endDate).orElse(null);
            QComment comment = QComment.comment;
            List<BooleanExpression> predicates = new ArrayList<>();
            Optional.ofNullable(f.getBlogId()).ifPresent(p -> predicates.add(comment.blogId.eq(p)));
            Optional.ofNullable(f.getInReplyToCommentId()).ifPresent(p -> predicates.add(comment.inReplyToComment.id.eq(p)));
            Optional.ofNullable(f.getCreatedBy()).ifPresent(p -> predicates.add(comment.createdBy.eq(p)));
            Optional.ofNullable(startDate).ifPresent(p -> predicates.add(comment.created.after(p)));
            Optional.ofNullable(endDate).ifPresent(p -> predicates.add(comment.created.before(p)));
            Iterator<BooleanExpression> iterator = predicates.listIterator();
            BooleanExpression all = iterator.next();
            while (iterator.hasNext()) {
                all = all.and(iterator.next());
            }

            Page<Comment> page = commentRepository.findAll(all, pageable);
            return page.map(c ->
                    new CommentDto(
                            c.getId(),
                            c.getBlogId(),
                            Optional.ofNullable(c.getInReplyToComment()).map(Comment::getId).orElse(null),
                            c.getTitle(),
                            c.getContent(),
                            c.getCreatedBy(),
                            c.getCreated()
                    )
            );
        }
        return commentRepository.findAllAsDtoBy(pageable);
    }

    private Date startDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    private Date endDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return calendar.getTime();
    }

    public Optional<CommentDto> comment(Long id) {
        return commentRepository.findAsDtoById(id);
    }

    private CommentDto commentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getBlogId(),
                Optional.ofNullable(comment.getInReplyToComment())
                        .map(Comment::getId)
                        .orElse(null),
                comment.getTitle(),
                comment.getContent(),
                comment.getCreatedBy(),
                comment.getCreated()
        );
    }
}
