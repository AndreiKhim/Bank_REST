package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import org.springframework.stereotype.Component;

// Преобразование User → UserResponse
@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhoneNumber());
        userResponse.setRole(user.getRole().name());
        return userResponse;
    }
}




