package com.example.miniproject.controller;

import com.example.miniproject.dto.UserRequestDto;
import com.example.miniproject.dto.UserResponseDto;
import com.example.miniproject.security.UserDetailsImpl;
import com.example.miniproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

//변경 by ym
@CrossOrigin(origins="*", exposedHeaders = "ACCESS_KEY")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/api/signup")
    public UserResponseDto signup(@RequestBody UserRequestDto requestDto) {
        return userService.singup(requestDto);
    }

    //로그인
    @PostMapping("/api/login")
    public UserResponseDto login(@RequestBody UserRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    //로그아웃
    @PostMapping("/api/logout")
    public UserResponseDto logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        return userService.logout(userDetails.getUser(), request);
    }

    //유저 아이디 반환
    @GetMapping("/api/user-info")
    public String getUserName(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUsername();
    }
}
