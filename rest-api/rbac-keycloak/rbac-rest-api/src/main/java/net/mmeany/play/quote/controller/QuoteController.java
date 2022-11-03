package net.mmeany.play.quote.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import net.mmeany.play.quote.controller.model.QuoteDto;
import net.mmeany.play.quote.controller.service.QuoteService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/quote")
@Validated
@Slf4j
public class QuoteController {

    final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Operation(summary = "Fetch a random quote")
    @GetMapping(value = {"", "/member", "/manager", "/admin"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuoteDto> quote() {
        dumpAuthorities("quote()");
        return ResponseEntity.ok(quoteService.nextQuote());
    }

    private void dumpAuthorities(String method) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = "Unknown";
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt jwt) {
            username = jwt.getClaim("preferred_username");
        }
        if (principal instanceof User user) {
            username = user.getUsername();
        }
        log.debug("'{}' invoked by '{}' with authorities: '{}'", method, username, auth.getAuthorities().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
    }
}
