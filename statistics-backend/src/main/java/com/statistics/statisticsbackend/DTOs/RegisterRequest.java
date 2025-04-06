package com.statistics.statisticsbackend.DTOs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
}
