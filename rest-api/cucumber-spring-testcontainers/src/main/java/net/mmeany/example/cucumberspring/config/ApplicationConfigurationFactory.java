package net.mmeany.example.cucumberspring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfigurationFactory {

    @Bean
    @ConfigurationProperties(prefix = "app-config")
    public ApplicationConfiguration getDefaultConfigs() {
        return new ApplicationConfiguration();
    }
}
