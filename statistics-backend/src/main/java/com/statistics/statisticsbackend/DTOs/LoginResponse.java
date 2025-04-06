package com.statistics.statisticsbackend.DTOs;

import com.statistics.statisticsbackend.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private User user;
    private String tokenString;
}
