package com.example.miniproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String userid;
    private String password;
    private boolean admin = false;
    private String adminToken;
}
