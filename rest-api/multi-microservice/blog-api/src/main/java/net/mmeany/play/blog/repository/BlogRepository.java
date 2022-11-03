package net.mmeany.play.blog.repository;

import net.mmeany.play.blog.model.Blog;
import net.mmeany.play.blog.model.BlogDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long>, QuerydslPredicateExecutor<Blog> {

    Optional<BlogDto> findAsDtoById(Long id);
}
