package net.mmeany.play.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final String[] allowedOriginPatterns;

    /**
     * Configure CORS.
     *
     * @param allowedOriginPatterns list of allowed origin patters, defaults to any origin
     */
    public WebMvcConfiguration(@Value("${mvm.cors.allowed-origins:*}") String[] allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(HttpMethod.GET.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.POST.name())
                .allowedOriginPatterns(allowedOriginPatterns);
    }
}
