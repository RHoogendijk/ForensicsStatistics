package com.statistics.statisticsbackend.services;

import com.statistics.statisticsbackend.DTOs.StudentDTO;
import com.statistics.statisticsbackend.models.Role;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public User findById(Long id){
        logger.info("Fetching user with id: {}", id);
        return userRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("User with id: {} not found!", id);
                    return new NoSuchElementException("User with id: " + id + " not found!");
                });
    }

    public User findByUploadCode(String uploadCode){
        logger.info("Fetching user with upload code: {}", uploadCode);
        return userRepository.findUserByUploadCode(uploadCode).orElseThrow(
                () -> {
                    logger.error("User with upload code: {} not found!", uploadCode);
                    return new NoSuchElementException("User with upload code: " + uploadCode + " not found!");
                }
        );
    }

    public User findByEmail(String email){
        logger.info("Fetching user with email: {}", email);
        return userRepository.findUserByEmail(email).orElse(null);
    }

    public List<User> findAll(){
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    public List<StudentDTO> findAllByClass(String classId){
        logger.info("Fetching all students by class");
        List<User> students = userRepository.findAllByRoleAndSchoolClass_Id(Role.STUDENT,  classId);
        return students.stream()
                .map(student -> {
                    Long id = student.getId();
                    String name = student.getFullName();
                    LocalDate localDate = LocalDate.of(2024, 12, 25);
                    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    return new StudentDTO(id, name, date);
                })
                .sorted(Comparator.comparing(StudentDTO::getNewestSessionDate))
                .collect(Collectors.toList());
    }


    public boolean existsByEmail(String email){
        logger.info("Checking if user with email: {} exists", email);
        return userRepository.existsByEmail(email);
    }

    public void save(User user){
        logger.info("Saving user with username: {}", user.getFullName());
        userRepository.save(user);
        logger.info("User saved successfully");
    }

}
