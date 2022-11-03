package net.mmeany.play.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import net.mmeany.play.comment.controller.model.CommentRequestImpl;
import net.mmeany.play.comment.controller.model.SearchFilter;
import net.mmeany.play.comment.model.CommentDto;
import net.mmeany.play.comment.service.CommentService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Fetch all comments, optionally filtered and sorted")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CommentDto>> allComments(@ParameterObject @Valid SearchFilter searchFilter, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(commentService.comments(searchFilter, pageable));
    }

    @Operation(summary = "Create a new comment")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDto> save(@Valid @RequestBody CommentRequestImpl commentRequest) {
        return new ResponseEntity<>(commentService.create(commentRequest.blogId(), commentRequest.inReplyToCommentId(), commentRequest.title(), commentRequest.content()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch a specific comment by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDto> comment(@Parameter(description = "Comment id") @Positive @PathVariable("id") Long id) {
        return ResponseEntity.of(commentService.comment(id));
    }
}
