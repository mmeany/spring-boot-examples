package net.mmeany.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app-config")
@Data
public class ApplicationConfiguration {
    private String[] allowedOrigins;
    private String id;
    private Boolean securityEnabled;
    private List<UserRecord> users;

    record UserRecord(String username,
                      String password,
                      String encrypted,
                      String[] roles) {
    }
}
