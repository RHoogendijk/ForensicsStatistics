package com.statistics.statisticsbackend.services;

import com.statistics.statisticsbackend.models.SchoolClass;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.repositories.SchoolClassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolClassService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SchoolClassRepository schoolClassRepository;
    private final UserService userService;

    public SchoolClass findById(String id) {
        logger.info("Fetching class with id: {}", id);
        return schoolClassRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("Class with id: {} not found!", id);
                    return new NoSuchElementException("Class with id: " + id + " not found!");
                });
    }

    public List<SchoolClass> findAll() {
        return schoolClassRepository.findAll();
    }

    public SchoolClass enroll(String classId, Long studentId) {
        User student = userService.findById(studentId);
        SchoolClass schoolClass = findById(classId);

        SchoolClass oldClass = student.getSchoolClass();
        if(oldClass != null){
            oldClass.removeUser(student);
        }
        schoolClass.addUser(student);
        userService.save(student);
        return schoolClass;
    }

    public void save(SchoolClass schoolClass) {
        logger.info("Saving class with id: {}", schoolClass.getId());
        schoolClassRepository.save(schoolClass);
        logger.info("Class saved successfully");
    }
}
