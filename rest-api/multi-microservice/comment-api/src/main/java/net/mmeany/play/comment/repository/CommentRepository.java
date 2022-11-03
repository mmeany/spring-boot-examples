package net.mmeany.play.comment.repository;

import net.mmeany.play.comment.model.Comment;
import net.mmeany.play.comment.model.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    Optional<CommentDto> findAsDtoById(Long id);

    Page<CommentDto> findAllAsDtoBy(Pageable pageable);
}
