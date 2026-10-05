package com.example.users;

import com.example.users.dto.RegistrationRequest;
import com.example.users.dto.UserResponse;
import com.example.users.entity.User;
import com.example.users.repository.UserRepository;
import com.example.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser() {

        when(userRepository.existsByEmail("raj@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("secret123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserService service = new UserService(userRepository, passwordEncoder);

        RegistrationRequest request = new RegistrationRequest(
                "Raj",
                "Padala",
                "raj@example.com",
                "secret123",
                "Nottingham",
                LocalDate.of(1990, 1, 1));

        UserResponse response = service.register(request);

        assertThat(response.email())
                .isEqualTo("raj@example.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getPassword())
                .isEqualTo("encoded-password");
    }
}