package com.example.bankcards.controller;

import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User("Doe", "John", "john@example.com", "123456", "pass", Role.USER);
        User user2 = new User("Smith", "Jane", "jane@example.com", "654321", "pass", Role.ADMIN);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        UserResponse resp1 = new UserResponse();
        UserResponse resp2 = new UserResponse();

        when(userMapper.toUserResponse(user1)).thenReturn(resp1);
        when(userMapper.toUserResponse(user2)).thenReturn(resp2);

        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserById() {
        User user = new User("Doe", "John", "john@example.com", "123456", "pass", Role.USER);
        when(userService.getUserById(1L)).thenReturn(user);
        UserResponse resp = new UserResponse();
        when(userMapper.toUserResponse(user)).thenReturn(resp);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(resp, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        ResponseEntity<String> response = userController.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Пользователь зарегистрирован", response.getBody());
        verify(userService).createUser(request);
    }

    @Test
    void testDeleteUser() {
        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).deleteUserById(1L);
    }
}
