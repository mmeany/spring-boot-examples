package net.mmeany.play.blog.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import net.mmeany.play.blog.controller.model.SearchFilter;
import net.mmeany.play.blog.model.Blog;
import net.mmeany.play.blog.model.BlogDto;
import net.mmeany.play.blog.model.QBlog;
import net.mmeany.play.blog.repository.BlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Transactional
    public BlogDto create(String title, String content, Set<String> tags) {
        return blogToDto(blogRepository.save(Blog.builder()
                .title(title)
                .content(content)
                .tags(tags)
                .build()));
    }

    public Page<BlogDto> blogs(SearchFilter f, Pageable pageable) {
        // TODO: JOIN FETCH or figure out how to use a JSON columns for the embeddable
        Page<Blog> page;
        if (f.hasFilters()) {
            Date startDate = Optional.ofNullable(f.getCreatedAfter()).map(this::startDate).orElse(null);
            Date endDate = Optional.ofNullable(f.getCreatedBefore()).map(this::endDate).orElse(null);
            QBlog blog = QBlog.blog;
            List<BooleanExpression> predicates = new ArrayList<>();
            Optional.ofNullable(f.getTitle()).ifPresent(p -> predicates.add(blog.title.lower().like(toLike(p))));
            Optional.ofNullable(f.getContent()).ifPresent(p -> predicates.add(blog.content.lower().like(toLike(p))));
            Optional.ofNullable(f.getCreatedBy()).ifPresent(p -> predicates.add(blog.createdBy.eq(p)));
            Optional.ofNullable(startDate).ifPresent(p -> predicates.add(blog.created.after(p)));
            Optional.ofNullable(endDate).ifPresent(p -> predicates.add(blog.created.before(p)));
            Optional.ofNullable(f.getTags()).ifPresent(p -> p.forEach(pp -> predicates.add(blog.tags.contains(pp))));

            Iterator<BooleanExpression> iterator = predicates.listIterator();
            BooleanExpression all = iterator.next();
            while (iterator.hasNext()) {
                all = all.and(iterator.next());
            }
            page = blogRepository.findAll(all, pageable);
        } else {
            page = blogRepository.findAll(pageable);
        }
        return page.map(b ->
                new BlogDto(
                        b.getId(),
                        b.getTitle(),
                        b.getContent(),
                        String.join(",", b.getTags()),
                        b.getCreatedBy(),
                        b.getCreated()
                )
        );
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

    private String toLike(String like) {
        StringBuilder sb = new StringBuilder();
        if (!like.startsWith("%")) {
            sb.append("%");
        }
        sb.append(like.toLowerCase());
        if (!like.endsWith("%")) {
            sb.append("%");
        }
        return sb.toString();
    }

    public Optional<BlogDto> blog(Long id) {
        BooleanExpression predicate = QBlog.blog.id.eq(id);

        Iterable<Blog> found = blogRepository.findAll(predicate);
        return found.iterator().hasNext()
                ? Optional.of(blogToDto(found.iterator().next()))
                : Optional.empty();
    }

    private BlogDto blogToDto(Blog blog) {
        return new BlogDto(
                blog.getId(),
                blog.getTitle(),
                blog.getContent(),
                String.join(",", blog.getTags()),
                blog.getCreatedBy(),
                blog.getCreated()
        );
    }
}
