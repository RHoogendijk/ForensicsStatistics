package com.statistics.statisticsbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private Date newestSessionDate;
}
