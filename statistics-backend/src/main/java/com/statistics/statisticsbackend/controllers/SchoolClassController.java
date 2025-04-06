package com.statistics.statisticsbackend.controllers;

import com.statistics.statisticsbackend.DTOs.SchoolClassCreationDTO;
import com.statistics.statisticsbackend.DTOs.StudentDTO;
import com.statistics.statisticsbackend.models.SchoolClass;
import com.statistics.statisticsbackend.models.User;
import com.statistics.statisticsbackend.services.SchoolClassService;
import com.statistics.statisticsbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private static final Logger logger = Logger.getLogger(SchoolClassController.class.getName());
    private final SchoolClassService schoolClassService;
    private final UserService userService;

    @GetMapping("")
    public List<SchoolClass> getAllClasses(){
        try {
            List<SchoolClass> schoolClasses = schoolClassService.findAll();
            logger.info("Found " + schoolClasses.size() + " classes");
            return schoolClasses;
        } catch (Exception e) {
            logger.severe("Error fetching classes: " + e.getMessage());
            throw e;
        }
    }

    //TODO: check for duplicates
    @PostMapping
    public ResponseEntity<SchoolClass> createClass(@RequestBody SchoolClassCreationDTO schoolClassCreationDTO){
        SchoolClass schoolClass = new SchoolClass(schoolClassCreationDTO.getId(), new ArrayList<>());
        schoolClassService.save(schoolClass);
        return ResponseEntity.ok(schoolClass);
    }

    @PostMapping("/{id}/enroll/{studentId}")
    public ResponseEntity<SchoolClass> enrollStudent(@PathVariable String id, @PathVariable Long studentId){
        SchoolClass updatedClass = schoolClassService.enroll(id, studentId);

        return ResponseEntity.ok(updatedClass);
    }

    @GetMapping("/{id}")
    public List<StudentDTO> getStudentsForClass(@PathVariable String id){
        logger.info("Retrieving students for class: " + id);
        return userService.findAllByClass(id);
    }

}
