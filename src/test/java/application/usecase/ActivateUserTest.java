package application.usecase;

import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ActivateUserTest {

    private UserRepository userRepository;
    private ActivateUser activateUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        activateUser = new ActivateUser(userRepository);
    }

    @Test
    @DisplayName("Should activate all PENDING users found")
    void shouldActivatePendingUsers() {
        // Arrange
        User pendingUser1 = User.create("user1@test.com", "pass123", LocalDateTime.now());
        User pendingUser2 = User.create("user2@test.com", "pass123", LocalDateTime.now());

        // Simulamos que la DB devuelve 2 usuarios pendientes
        when(userRepository.findByStatus(UserStatus.PENDING))
                .thenReturn(List.of(pendingUser1, pendingUser2));

        // Act
        activateUser.execute(); // Ejecutamos el Job manualmente

        // Assert
        assertEquals(UserStatus.ACTIVE, pendingUser1.getStatus());
        assertEquals(UserStatus.ACTIVE, pendingUser2.getStatus());

        // Verificamos que se llamó a save() 2 veces (una por cada usuario)
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("Should do nothing if no PENDING users found")
    void shouldDoNothingIfListEmpty() {
        // Arrange
        when(userRepository.findByStatus(UserStatus.PENDING)).thenReturn(Collections.emptyList());

        // Act
        activateUser.execute();

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }
}