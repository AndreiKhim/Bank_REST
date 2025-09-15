package com.example.bankcards.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private List<CardResponse> cards;
}