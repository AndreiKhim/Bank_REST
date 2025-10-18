//package com.example.bankcards.controller;
//
//import com.example.bankcards.dto.*;
//import com.example.bankcards.entity.User;
//import com.example.bankcards.entity.enums.Role;
//import com.example.bankcards.repository.UserRepository;
//import com.example.bankcards.security.CustomUserDetails;
//import com.example.bankcards.service.UserService;
//import com.example.bankcards.util.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AuthControllerTest {
//
//    @InjectMocks
//    private AuthController authController;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    private AutoCloseable closeable;
//
//    @BeforeEach
//    void setUp() {
//        closeable = MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testRegisterSuccess() {
//        RegisterRequest request = new RegisterRequest();
//        request.setEmail("test@example.com");
//        request.setPassword("password");
//        request.setFirstName("John");
//        request.setLastName("Doe");
//        request.setPhoneNumber("1234567890");
//
//        User newUser = new User("Петров", "Петр", "test@example.com", "1234567890", "encodedPass", Role.USER);
//
//        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());
//        when(userService.createUser(request)).thenReturn(newUser);
//        when(jwtUtil.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");
//
//        ResponseEntity<?> response = authController.register(request);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue(response.getBody() instanceof AuthResponse);
//        assertEquals("jwt-token", ((AuthResponse) response.getBody()).getToken());
//    }
//
//    @Test
//    void testRegisterEmailExists() {
//        RegisterRequest request = new RegisterRequest();
//        request.setEmail("test@example.com");
//
//        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(new User()));
//
//        ResponseEntity<?> response = authController.register(request);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("Пользователь с таким email уже существует", response.getBody());
//    }
//
//    @Test
//    void testLoginSuccess() {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("test@example.com");
//        request.setPassword("password");
//
//        User user = new User("Петров", "Петр", "test@example.com", "1234567890", "encodedPass", Role.USER);
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
//        when(jwtUtil.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");
//
//        ResponseEntity<AuthResponse> response = authController.login(request);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("jwt-token", response.getBody().getToken());
//    }
//
//    @Test
//    void testLoginBadPassword() {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("test@example.com");
//        request.setPassword("wrong");
//
//        User user = new User("Петров", "Петр", "test@example.com", "1234567890", "encodedPass", Role.USER);
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);
//
//        assertThrows(BadCredentialsException.class, () -> authController.login(request));
//    }
//}
