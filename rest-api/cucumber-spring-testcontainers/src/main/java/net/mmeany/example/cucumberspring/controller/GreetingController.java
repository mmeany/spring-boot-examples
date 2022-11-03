package net.mmeany.example.cucumberspring.controller;

import net.mmeany.example.cucumberspring.controller.model.GreetingRequest;
import net.mmeany.example.cucumberspring.controller.model.GreetingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
public class GreetingController {

    @PostMapping({"/hello", "/greet", "/member/greet", "/manager/greet", "/admin/greet"})
    public ResponseEntity<GreetingResponse> sayHello(@RequestBody @Valid GreetingRequest request) {
        return ResponseEntity.ok(GreetingResponse.builder()
                .name(request.getName())
                .greeting("Hello " + (isBlank(request.getName()) ? "World!" : request.getName()))
                .build());
    }

}
