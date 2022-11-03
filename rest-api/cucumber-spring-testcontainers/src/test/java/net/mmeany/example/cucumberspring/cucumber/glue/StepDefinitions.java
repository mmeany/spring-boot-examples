package net.mmeany.example.cucumberspring.cucumber.glue;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.mmeany.example.cucumberspring.controller.model.GreetingRequest;
import net.mmeany.example.cucumberspring.controller.model.GreetingResponse;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@Slf4j
public class StepDefinitions extends BaseScenario {

    @BeforeAll
    public static void beforeAll() {
        log.debug("----- Wipeout -----");
    }

    @Given("^user ([A-Za-z]*)$")
    public void aUser(String name) {
        ctx.reset();
        ctx.setUserName(name);
    }

    @When("^the user calls the (.*) endpoint$")
    public void userCallsEndpoint(String endpoint) {
        ctx.setEndpoint(endpoint);
    }

    @Then("^the call will (.*) with status code (.*) and (.*)$")
    public void endpointResult(String result, Integer status, String greeting) {

        GreetingRequest request = GreetingRequest.builder()
                .name(ctx.getUserName())
                .build();

        ApiResponse<GreetingResponse> response =
                postToApi(ctx.getEndpoint(), getCredentials(ctx.getUserName()), Map.of(), request, GreetingResponse.class, HttpStatus.OK);

        assertThat(response.getStatus(), is(HttpStatus.valueOf(status)));

        if (result.equalsIgnoreCase("fail")) {
            assertThat(response.getErrored(), is(true));
            assertThat(response.getResponse(), nullValue());
        } else {
            assertThat(response.getErrored(), is(false));
            assertThat(response.getResponse().getGreeting(), is(greeting));
        }
    }
}
