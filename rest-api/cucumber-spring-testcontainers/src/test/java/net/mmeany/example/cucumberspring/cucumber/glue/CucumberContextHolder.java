package net.mmeany.example.cucumberspring.cucumber.glue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Data
@Component
@Profile("test-containers")
@Slf4j
public class CucumberContextHolder {

    public final String keycloakBaseUrl;

    public CucumberContextHolder() {
        keycloakBaseUrl = SpringBootTestContainersLoader.KEYCLOAK.getAuthServerUrl();
    }

    private String userName;
    private String endpoint;
//    private String role;
//    private String method;
//    private Integer status;

    public CucumberContextHolder reset() {
        log.info("################################################# RESET Captured Variables ({})", this);
        userName = null;
        endpoint = null;
//        role = null;
//        method = null;
//        status = null;
        return this;
    }
}
