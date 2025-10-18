package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication auth) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + auth.getName()));
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            Authentication auth,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления профиля",
                    required = true
            )
            @RequestBody UserRequest request
    ) {
        User user = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + auth.getName()));

        User updatedUser = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(userMapper.toUserResponse(updatedUser));
    }
}
