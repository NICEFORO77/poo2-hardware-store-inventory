package com.niceforo.inventario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niceforo.inventario.auth.AuthService;
import com.niceforo.inventario.dto.AuthRequest;
import com.niceforo.inventario.dto.AuthResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
