package com.statistics.statisticsbackend.controllers;

import com.statistics.statisticsbackend.DTOs.LoginRequest;
import com.statistics.statisticsbackend.DTOs.LoginResponse;
import com.statistics.statisticsbackend.DTOs.RegisterRequest;
import com.statistics.statisticsbackend.exceptions.UnauthorizedException;
import com.statistics.statisticsbackend.exceptions.UserAlreadyExistsException;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.services.AuthenticationService;
import com.statistics.statisticsbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getName());

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest request) {
        logger.info("POST /auth/register: Attempting to register user.");
        try {
            authenticationService.register(request);
            logger.info("User registered successfully: " + request.getEmail());
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (UserAlreadyExistsException e) {
            logger.warning("User registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody LoginRequest request) {
        logger.info("POST /auth/login: Attempting to log in user.");
        try {
            LoginResponse loginResponse = authenticationService.login(request);
            User user = loginResponse.getUser();
            String tokenString = loginResponse.getTokenString();

            logger.info("User logged in successfully: " + user.getEmail());
            logger.info("User role: " + user.getRole());
            return ResponseEntity.accepted()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenString)
                    .body(Map.of(
                            "user", user,
                            "message", "User logged in successfully"
                    ));
        } catch (UnauthorizedException e) {
            logger.warning("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.severe("Unexpected error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred. Please try again later."));
        }
    }
}
