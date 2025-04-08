package com.statistics.statisticsbackend;

import com.statistics.statisticsbackend.models.Role;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.security.SecureHasher;
import com.statistics.statisticsbackend.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StatisticsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner createAdminAccount(UserService userService) {
        return args -> {
            byte[] salt = SecureHasher.generateSalt();

            User user = new User();
            user.setFullName("Admin");
            user.setEmail("admin@example.com");
            user.setSalt(salt);
            user.setPassword("Welkom1@");
            user.setRole(Role.ADMIN);

            userService.save(user);
        };
    }

}
