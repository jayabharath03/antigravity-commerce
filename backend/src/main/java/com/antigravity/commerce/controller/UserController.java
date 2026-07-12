package com.antigravity.commerce.controller;

import com.antigravity.commerce.dto.ApiResponse;
import com.antigravity.commerce.dto.UserDto;
import com.antigravity.commerce.entity.User;
import com.antigravity.commerce.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserDto userDto = userMapper.toDto(currentUser);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User details fetched successfully"));
    }
}
