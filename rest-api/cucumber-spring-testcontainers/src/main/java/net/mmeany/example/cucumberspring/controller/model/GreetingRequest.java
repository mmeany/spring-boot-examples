package net.mmeany.example.cucumberspring.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;

@Value
@Jacksonized
@Builder
public class GreetingRequest {
    @Size(min = 2, max = 20)
    String name;
}
