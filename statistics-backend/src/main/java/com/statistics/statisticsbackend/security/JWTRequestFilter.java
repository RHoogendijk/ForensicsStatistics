package com.statistics.statisticsbackend.security;

import com.statistics.statisticsbackend.APIConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private APIConfig apiConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //get the encrypted token string from the request header
        String encryptedToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        String servletPath = request.getServletPath();

        //OPTIONS requests should pass without any check
        if (HttpMethod.OPTIONS.matches(request.getMethod()) ) {
            //proceed along the chain of filters towards the REST controller.
            filterChain.doFilter(request, response);
            return;
        }

        boolean isSecuredPath = this.apiConfig.SECURED_PATHS.stream()
                .anyMatch(path -> new AntPathMatcher().match(path, servletPath));

        if (isSecuredPath && encryptedToken == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "no token provided.");
            return;
        }
        JWToken jwToken;
        if (encryptedToken != null) {
            try {
                //decode the token after removing Bearer prefix
                jwToken = JWToken.decode(encryptedToken.replace("Bearer ", ""), this.apiConfig.getIssuer(), this.apiConfig.getPassphrase());
                //pass-on token info as request attribute
                request.setAttribute(JWToken.JWT_ATTRIBUTE_NAME, jwToken);

            } catch (RuntimeException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        e.getMessage() + "invalid token");
            }
        }
        //proceed along the chain of filters towards the REST controller.
        filterChain.doFilter(request, response);
    }
}
