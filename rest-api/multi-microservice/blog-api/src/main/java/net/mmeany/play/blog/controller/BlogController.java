package net.mmeany.play.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import net.mmeany.play.blog.controller.model.BlogRequestImpl;
import net.mmeany.play.blog.controller.model.SearchFilter;
import net.mmeany.play.blog.model.BlogDto;
import net.mmeany.play.blog.service.BlogService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blog")
@Validated
@Slf4j
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @Operation(summary = "Fetch all blogs, optionally filtered and sorted")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<BlogDto>> allBlogs(@ParameterObject @Valid SearchFilter searchFilter, @ParameterObject Pageable pageable) {
        log.debug("FindAll: '{}'", searchFilter);
        dumpAuthorities();
        return ResponseEntity.ok(blogService.blogs(searchFilter, pageable));
    }

    @Operation(summary = "Create a new blog")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlogDto> save(@Valid @RequestBody BlogRequestImpl blogRequest) {
        log.debug("Create: '{}'", blogRequest);
        dumpAuthorities();
        return new ResponseEntity<>(blogService.create(blogRequest.title(), blogRequest.content(), blogRequest.tags()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch a specific blog by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlogDto> blog(@Parameter(description = "Blog id") @Positive @PathVariable("id") Long id) {
        log.debug("FindById: '{}'", id);
        dumpAuthorities();
        return ResponseEntity.of(blogService.blog(id));
    }

    private void dumpAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authorities derived from the JWT: '{}'", auth.getAuthorities().stream().map(String::valueOf).collect(Collectors.joining(",")));
    }
}
