package com.antigravity.commerce.service;

import com.antigravity.commerce.dto.AuthResponse;
import com.antigravity.commerce.dto.LoginRequest;
import com.antigravity.commerce.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
