package model;

import domain.model.User;
import domain.model.UserStatus;
import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now();
    }

    @Test
    @DisplayName("successful_User_Creation")
    void successful_User_Creation() {
        User user = User.create("user1@test.com", "user1.1234", createdAt);

        assertNotNull(user);
        assertEquals(UserStatus.PENDING, user.getStatus());
        assertEquals("user1@test.com", user.getEmail());
        assertTrue(user.getPassword().length() > 4);
    }

    @Test
    @DisplayName("activate_User")
    public void activate_User() {
        User user = User.create("user1@test.com", "user1.1234", createdAt);

        assertNotNull(user);

        assertEquals(UserStatus.PENDING, user.getStatus());
        assertFalse(user.isActive());

        user.activate();

        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("email_and_password_attribute_failure")
    public void failure_Email_And_Password() {
        // Empty email
        Exception exceptionEmail = assertThrows(BusinessRuleViolationsException.class, () -> {
            User.create("", "user1.1234", createdAt);
        });
        assertEquals("El email es requerido", exceptionEmail.getMessage());

        // Empty password
        Exception exceptionPassw = assertThrows(BusinessRuleViolationsException.class, () -> {
            User.create("user1@test.com", "", createdAt);
        });
        assertEquals("La contraseña invalida", exceptionPassw.getMessage());
    }

    @Test
    @DisplayName("only_pending_users_are_activated")
    public void only_pending_users_are_activated() {
        User user = User.create("user1@test.com", "user1.1234", createdAt);

        user.activate();

        BusinessRuleViolationsException exceptionActivate = assertThrows(
                BusinessRuleViolationsException.class, user::activate
        );

        assertEquals("Solo usuarios pendientes pueden ser activados", exceptionActivate.getMessage());
    }
}