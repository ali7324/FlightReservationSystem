package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.AuthDto;
import com.example.flightreservationsystem.entity.UserEntity;
import com.example.flightreservationsystem.enums.Role;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.repository.UserRepository;
import com.example.flightreservationsystem.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    //register
    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("Yeni istifadəçi: email unikaldır, rol null -> USER, şifrə encode olunur, token qaytarılır")
        void register_success_defaultRole() {
            String email = "user@example.com";
            String rawPw = "secret";
            String encodedPw = "ENC(secret)";
            String token = "jwt-123";

            AuthDto request = AuthDto.builder()
                    .email(email)
                    .password(rawPw)
                    .role(null)
                    .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(rawPw)).thenReturn(encodedPw);
            when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
            when(jwtService.generateToken(any(UserEntity.class))).thenReturn(token);

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

            AuthDto result = authenticationService.register(request);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
            assertEquals(Role.USER, result.getRole());
            assertEquals(token, result.getToken());

            verify(userRepository).save(userCaptor.capture());
            UserEntity saved = userCaptor.getValue();
            assertEquals(email, saved.getEmail());
            assertEquals(Role.USER, saved.getRole());
            assertEquals(encodedPw, saved.getPassword());

            verify(userRepository).findByEmail(email);
            verify(passwordEncoder).encode(rawPw);
            verify(jwtService).generateToken(saved);


            verifyNoMoreInteractions(jwtService, passwordEncoder, userRepository);
        }

        @Test
        @DisplayName("Rol veriləndə: həmin rol saxlanılır")
        void register_success_givenRole() {
            String email = "admin@example.com";
            String rawPw = "pw";
            String encodedPw = "ENC(pw)";
            String token = "jwt-456";

            AuthDto request = AuthDto.builder()
                    .email(email)
                    .password(rawPw)
                    .role(Role.ADMIN)
                    .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(rawPw)).thenReturn(encodedPw);
            when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
            when(jwtService.generateToken(any(UserEntity.class))).thenReturn(token);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

            AuthDto res = authenticationService.register(request);

            assertEquals(Role.ADMIN, res.getRole());
            assertEquals(token, res.getToken());

            verify(userRepository).save(captor.capture());
            assertEquals(Role.ADMIN, captor.getValue().getRole());
        }

        @Test
        @DisplayName("Email artıq varsa: IllegalArgumentException atılır")
        void register_duplicateEmail_throws() {
            String email = "dup@example.com";
            AuthDto request = AuthDto.builder()
                    .email(email)
                    .password("x")
                    .build();

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(UserEntity.builder().email(email).build()));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> authenticationService.register(request));


            assertTrue(ex.getMessage().toLowerCase().contains("email"));
            verify(userRepository, never()).save(any());
            verify(jwtService, never()).generateToken(any());
        }
    }

    //login
    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Uğurlu login: authenticate çağırılır, user tapılır, token qaytarılır")
        void login_success() {
            String email = "u@example.com";
            String pw = "pw";
            String token = "jwt-login-1";
            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password("ENC")
                    .role(Role.USER)
                    .build();

            AuthDto request = AuthDto.builder()
                    .email(email)
                    .password(pw)
                    .build();

            Authentication auth = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(jwtService.generateToken(user)).thenReturn(token);

            AuthDto res = authenticationService.login(request);

            assertNotNull(res);
            assertEquals(email, res.getEmail());
            assertEquals(Role.USER, res.getRole());
            assertEquals(token, res.getToken());

            ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(tokenCaptor.capture());
            UsernamePasswordAuthenticationToken passed = tokenCaptor.getValue();
            assertEquals(email, passed.getPrincipal());
            assertEquals(pw, passed.getCredentials());

            verify(userRepository).findByEmail(email);
            verify(jwtService).generateToken(user);
        }

        @Test
        @DisplayName("User tapılmayanda: ResourceNotFoundException atılır")
        void login_userNotFound_throws() {
            String email = "missing@example.com";
            AuthDto request = AuthDto.builder()
                    .email(email)
                    .password("pw")
                    .build();

            when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> authenticationService.login(request));

            assertTrue(ex.getMessage().contains("User not found with email: " + email));
            verify(jwtService, never()).generateToken(any());
        }
    }
}
