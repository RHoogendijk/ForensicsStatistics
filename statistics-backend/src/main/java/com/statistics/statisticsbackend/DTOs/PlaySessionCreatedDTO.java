package com.statistics.statisticsbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class PlaySessionCreatedDTO {
    private Long sessionId;
    private String username;
}
