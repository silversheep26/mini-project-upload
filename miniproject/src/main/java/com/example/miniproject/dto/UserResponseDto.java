package com.example.miniproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String meassage;
    public UserResponseDto(String meassage){
        this.meassage = meassage;
    }
}

