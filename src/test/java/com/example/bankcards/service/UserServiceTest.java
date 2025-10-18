package com.example.bankcards.service;

import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("Doe", "John", "john@mail.com", "12345", "encodedPass", Role.USER);
        user.setId(1L);
    }

    @Test
    void registerUser_shouldEncodePasswordAndSaveUser() {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john@mail.com");
        req.setPhoneNumber("12345");
        req.setPassword("pass");
        req.setRole("USER");
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.registerUser(req);

        assertThat(saved.getEmail()).isEqualTo("john@mail.com");
        assertThat(saved.getPassword()).isEqualTo("encodedPass");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loadUserByUsername_shouldReturnSpringUserDetails() {
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));

        var details = userService.loadUserByUsername("john@mail.com");

        assertThat(details.getUsername()).isEqualTo("john@mail.com");
        assertThat(details.getAuthorities()).extracting("authority").contains("ROLE_USER");
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionIfNotFound() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loadUserByUsername("missing@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);
        assertThat(result.getEmail()).isEqualTo("john@mail.com");
    }

    @Test
    void getUserById_shouldThrowExceptionIfNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUser_shouldCallMapperAndSave() {
        UserRequest request = new UserRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("Jane@mail.com");
        request.setPhoneNumber("54321");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, request);

        verify(userMapper, times(1)).updateUserFromRequest(request, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUserById_shouldDeleteIfExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserById_shouldThrowIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findAll();
    }
}
