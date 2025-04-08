package com.statistics.statisticsbackend.controllers;

import com.statistics.statisticsbackend.exceptions.UnauthorizedException;
import com.statistics.statisticsbackend.models.Role;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.security.JWToken;
import com.statistics.statisticsbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @GetMapping("")
    public List<User> getAllUsers(@RequestAttribute(name = JWToken.JWT_ATTRIBUTE_NAME) JWToken jwtInfo) {
        logger.info("GET /users: Fetching all users");

        if (jwtInfo == null) {
            logger.warning("Unauthorized access attempt: Missing JWT token.");
            throw new UnauthorizedException("JWT token is required.");
        }

        if (jwtInfo.getRole() != Role.ADMIN ) {
            logger.warning("Unauthorized access attempt by user with role: " + jwtInfo.getRole());
            throw new UnauthorizedException("Admin role is required to view users.");
        }

        try {
            List<User> users = userService.findAll();
            logger.info("Successfully retrieved " + users.size() + " users.");
            return users;
        } catch (Exception e) {
            logger.severe("Error fetching users: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/{id}/role")
    public ResponseEntity<User> addRole(@PathVariable Long id, @RequestBody Role role, @RequestAttribute(name = JWToken.JWT_ATTRIBUTE_NAME) JWToken jwtInfo) {
        logger.info("POST /users/" + id + "/role: " + role);
        if (jwtInfo == null) {
            logger.warning("Unauthorized access attempt: Missing JWT token.");
            throw new UnauthorizedException("JWT token is required.");
        }

        if (jwtInfo.getRole() != Role.ADMIN ) {
            logger.warning("Unauthorized access attempt by user with role: " + jwtInfo.getRole());
            throw new UnauthorizedException("Admin role is required to change user roles");
        }
        try{
            User user = userService.findById(id);
            user.setRole(role);
            userService.save(user);
            return ResponseEntity.ok(user);
        } catch(NoSuchElementException e){
            logger.warning("User not found: " + id);
            return ResponseEntity.notFound().build();
        }
    }
}
