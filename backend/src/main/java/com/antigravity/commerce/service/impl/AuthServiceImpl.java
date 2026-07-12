package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.dto.AuthResponse;
import com.antigravity.commerce.dto.LoginRequest;
import com.antigravity.commerce.dto.RegisterRequest;
import com.antigravity.commerce.entity.RefreshToken;
import com.antigravity.commerce.entity.Role;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.exception.BadRequestException;
import com.antigravity.commerce.exception.ResourceNotFoundException;
import com.antigravity.commerce.exception.UnauthorizedException;
import com.antigravity.commerce.mapper.UserMapper;
import com.antigravity.commerce.repository.RefreshTokenRepository;
import com.antigravity.commerce.repository.RoleRepository;
import com.antigravity.commerce.repository.UserRepository;
import com.antigravity.commerce.security.JwtService;
import com.antigravity.commerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role CUSTOMER not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .isVerified(false)
                .build();
        user.getRoles().add(customerRole);

        user = userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String jwt = jwtService.generateToken(user);
        
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String jwt = jwtService.generateToken(user);
                    return AuthResponse.builder()
                            .accessToken(jwt)
                            .refreshToken(token)
                            .user(userMapper.toDto(user))
                            .build();
                })
                .orElseThrow(() -> new UnauthorizedException("Refresh token is not in database or is invalid!"));
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusNanos(refreshExpiration * 1000000)) // refreshExpiration is in ms
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new UnauthorizedException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
