package application.usecase;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegisterUserTest {

    private UserRepository userRepository;
    private RegisterUser registerUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        registerUser = new RegisterUser(userRepository);
    }

    @Test
    @DisplayName("Should register user successfully with PENDING status")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        String email = "newuser@test.com";
        String password = "password123";
        UserRequestDTO request = new UserRequestDTO(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        // Mock save returning the user with ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });


        // Act
        UserResponseDTO response = registerUser.registerUser(request);


        // Assert
        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("PENDING", response.status());


        // Verificamos que se guardó con el estado correcto
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(UserStatus.PENDING, capturedUser.getStatus());
        assertNotNull(capturedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should fail if email already exists")
    void shouldFailIfEmailExists() {
        UserRequestDTO request = new UserRequestDTO("exists@test.com", "pass123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(mock(User.class)));

        assertThrows(BusinessRuleViolationsException.class, () -> registerUser.registerUser(request));
        verify(userRepository, never()).save(any());
    }
}