package com.statistics.statisticsbackend.services;

import com.statistics.statisticsbackend.APIConfig;
import com.statistics.statisticsbackend.DTOs.LoginRequest;
import com.statistics.statisticsbackend.DTOs.LoginResponse;
import com.statistics.statisticsbackend.DTOs.RegisterRequest;
import com.statistics.statisticsbackend.exceptions.UnauthorizedException;
import com.statistics.statisticsbackend.exceptions.UserAlreadyExistsException;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.security.JWToken;
import com.statistics.statisticsbackend.security.SecureHasher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final APIConfig apiConfig;
    private final UserService userService;

    @Autowired
    public AuthenticationService(UserService userService, APIConfig apiConfig ) {
        this.userService = userService;
        this.apiConfig = apiConfig;
    }

    @PersistenceContext
    private EntityManager em;

    /**
     * Registers/signs-up a new user.
     * @param request the RegisterRequest containing the username, email, and password for the new user
     * @throws UserAlreadyExistsException if a user with the given username or email already exists
     */
    public void register(RegisterRequest request) {
        logger.info("Starting registration for username: {}", request.getFullName());


        if (!isValidEmail(request.getEmail())) {
            logger.error("Invalid email format: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email format");
        }

        //Check if a User with the given email already exists
        if (userService.existsByEmail(request.getEmail())) {
            logger.error("Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (!isValidPassword(request.getPassword())) {
            logger.error("Invalid password format for username: {}", request.getFullName());
            throw new IllegalArgumentException("Password must be at least 8 characters long, include at least one number, and one special character (!@#$%?^&*).");
        }

        logger.info("Password validation passed for username: {}", request.getFullName());

        //Generate a salt for the new user
        byte[] salt = SecureHasher.generateSalt();
        //Create a new user and save it to the database
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setSalt(salt);
        newUser.setPassword(request.getPassword());

        userService.save(newUser);
        logger.info("User registered successfully: {}", newUser.getFullName());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[!@#$%?^&*])[A-Za-z0-9!@#$%?^&*]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

    /**
     * logs-/signs-in an existing user
     * @param request the LoginRequest containing the email and password for an existing user
     * @return a LoginResponse containing the User object and a JWToken string
     * @throws UnauthorizedException if no user with the given email is found or the password does not match the found user
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("Attempting to log in with email: {}", request.getEmail());

        //tries to find an existing user by email
        User user =userService.findByEmail(request.getEmail());

        //checks for valid login information
        if (user == null || !user.verifyPassword(request.getPassword())) {
            logger.error("Login failed for email: {}", request.getEmail());
            throw new UnauthorizedException("Username or password do not match with an existing account");
        }

        // Issue a token for the account
        JWToken jwToken = new JWToken(user.getFullName(), user.getId(), user.getRole());
        String tokenString = jwToken.encode(this.apiConfig.getIssuer(), this.apiConfig.getTokenDurationOfValidity(), this.apiConfig.getPassphrase());

        logger.info("Login successful for email: {}", request.getEmail());
        return new LoginResponse(user, tokenString);
    }
}
