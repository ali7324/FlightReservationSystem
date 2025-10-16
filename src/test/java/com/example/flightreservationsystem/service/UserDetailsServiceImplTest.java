package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.entity.UserEntity;
import com.example.flightreservationsystem.enums.Role;
import com.example.flightreservationsystem.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserDetailsServiceImpl service;

    @Test
    @DisplayName("User tapıldıqda: UserDetails qaytarılır və sahələr düzgün doldurulur")
    void loadUserByUsername_found() {
        String email = "test@example.com";

        // Real entity quraq ki, getter-lər null olmasın
        UserEntity user = UserEntity.builder()
                .email(email)
                .password("ENC")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername(email);

        assertNotNull(details);
        assertEquals(email, details.getUsername()); // bizim layihədə username=email-dir
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("User tapılmadıqda: UsernameNotFoundException")
    void loadUserByUsername_notFound() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(email));
        verify(userRepository).findByEmail(email);
    }
}
