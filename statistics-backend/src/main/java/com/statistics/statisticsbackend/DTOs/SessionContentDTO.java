package com.statistics.statisticsbackend.DTOs;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SessionContentDTO {
    private String outsideBackgroundURL;
    private String basementBackgroundURL;
    private String jsonFileURL;
    private List<String> photoURLs;
    private List<String> evidence;
}
