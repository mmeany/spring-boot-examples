package net.mmeany.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ApplicationConfiguration applicationConfiguration;

    /**
     * Configure CORS.
     */
    public WebMvcConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS for origins: {}", applicationConfiguration.getAllowedOrigins());
        registry.addMapping("/**")
                .allowedMethods(HttpMethod.GET.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.POST.name())
                .allowedOriginPatterns(applicationConfiguration.getAllowedOrigins());
    }
}
