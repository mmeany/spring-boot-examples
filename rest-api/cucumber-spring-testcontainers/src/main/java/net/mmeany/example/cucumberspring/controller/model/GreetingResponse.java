package net.mmeany.example.cucumberspring.controller.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class GreetingResponse {
    String name;
    String greeting;
}
