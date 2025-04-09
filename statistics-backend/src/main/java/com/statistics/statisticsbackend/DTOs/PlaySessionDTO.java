package com.statistics.statisticsbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PlaySessionDTO {
    private Long sessionId;
    private Date createdTime;

}
