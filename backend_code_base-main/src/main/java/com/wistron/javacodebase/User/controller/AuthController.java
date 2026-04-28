package com.wistron.javacodebase.User.controller;

import com.wistron.javacodebase.User.dto.LoginRequest;
import com.wistron.javacodebase.User.dto.LoginResponse;
import com.wistron.javacodebase.User.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}