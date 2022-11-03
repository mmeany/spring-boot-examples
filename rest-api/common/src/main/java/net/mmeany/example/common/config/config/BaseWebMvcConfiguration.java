package net.mmeany.example.common.config.config;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class BaseWebMvcConfiguration implements WebMvcConfigurer {

    private final BaseApplicationConfiguration applicationConfiguration;

    /**
     * Configure CORS.
     */
    public BaseWebMvcConfiguration(BaseApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(HttpMethod.GET.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.POST.name())
                .allowedOriginPatterns(applicationConfiguration.getAllowedOrigins());
    }
}
