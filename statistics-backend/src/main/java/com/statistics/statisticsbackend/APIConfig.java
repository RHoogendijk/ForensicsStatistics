package com.statistics.statisticsbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class APIConfig implements WebMvcConfigurer
{
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.passphrase}")
    private String passphrase;
    @Value("${jwt.duration-of-validity}")
    private int tokenDurationOfValidity;

    public Set<String> SECURED_PATHS =
            Set.of("/userdata");

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("haha ur not getting my ip")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders(
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.CONTENT_TYPE
                )
                .exposedHeaders(
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.CONTENT_TYPE
                )
                .allowCredentials(true);
    }

    public String getIssuer() {
        return issuer;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public int getTokenDurationOfValidity() {
        return tokenDurationOfValidity;
    }
}
