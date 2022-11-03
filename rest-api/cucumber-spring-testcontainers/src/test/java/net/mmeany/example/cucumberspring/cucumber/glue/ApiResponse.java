package net.mmeany.example.cucumberspring.cucumber.glue;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private Boolean errored = false;
    private HttpStatus status;
    private T response;
    private String errorBody;
    private String errorMessage;
}
