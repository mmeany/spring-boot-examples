package net.mmeany.example.controller;

import lombok.extern.slf4j.Slf4j;
import net.mmeany.example.config.ApplicationConfiguration;
import net.mmeany.example.controller.model.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class ApiController {

    private final ApplicationConfiguration applicationConfiguration;

    public ApiController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @GetMapping("/hello/{name}")
    public ResponseEntity<Response> hello(
            @PathVariable("name")
            @Size(min = 2, max = 20, message = "Name must be betwen 2 and 20 characters")
            String name
    ) {
        log.debug("Configuration:\n{}", applicationConfiguration);
        return ResponseEntity.ok(new Response(applicationConfiguration.getId(), "Hello " + name));
    }

    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> showMeAllHeaders(HttpServletRequest request) {
        log.debug("Configuration:\n{}", applicationConfiguration);
        Map<String, String> headers = Collections.list(request.getHeaderNames()).stream()
                .map(hn -> new NameValue(hn, request.getHeader(hn)))
                .collect(Collectors.toMap(NameValue::name, NameValue::value));
        return ResponseEntity.ok(headers);
    }

    record NameValue(
            String name,
            String value
    ) {}

}
