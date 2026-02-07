package com.edubridge.backend.controller;

import com.edubridge.backend.dto.request.LoginRequest;
import com.edubridge.backend.dto.response.TokenResponse;
import com.edubridge.backend.repository.UserRepository;
import com.edubridge.backend.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // ✅ No try-catch - Spring Security and GlobalExceptionHandler handle errors
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtUtil.generateToken(loginRequest.getEmail());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}