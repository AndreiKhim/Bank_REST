package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserRegistrationResponse;
import com.example.bankcards.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public UserRegistrationResponse toResponse(User user) {
        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhoneNumber());
        response.setRole(user.getRole().name());
        return response;
    }
}