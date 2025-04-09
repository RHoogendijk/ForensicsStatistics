package com.statistics.statisticsbackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaySessionListDTO {
    private String username;
    private List<PlaySessionDTO> playSessions;

}
