package model;

import infrastructure.exception.BusinessRuleViolationsException;
import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import domain.model.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {

    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime activationExpiresAt;

    @BeforeEach
    void setUp(){
        createdAt = LocalDateTime.now();
    }

    @Test
    @Order(1)
    @DisplayName("successful_User_Creation")
    void successful_User_Creation(){

        user = User.create("user1@test.com",
                "user1.1234",
                createdAt);

        assertNotNull(user);
        assertEquals(UserStatus.PENDING, user.getStatus());
        assertEquals("user1@test.com", user.getEmail());
        assertTrue(user.getPassword().length()>4);

    }

    @Test
    @Order(2)
    @DisplayName("activate_User")
    public void activate_User(){

        user = User.create("user1@test.com",
                "user1.1234",
                createdAt);

        assertNotNull(user);
        user.activate();
        assertEquals(UserStatus.ACTIVE, user.getStatus());

    }

    @Test
    @Order(3)
    @DisplayName("email_and_password_attribute_failure")
    public void failure_Email_And_Password(){

       Exception exceptionEmail = assertThrows(Exception.class, () -> {
            User.create("","user1.1234",createdAt);
        });
       String messageEmail = exceptionEmail.getMessage();
       assertEquals(messageEmail,"El email es requerido");

       System.out.println(messageEmail);

        Exception exceptionPassw = assertThrows(Exception.class, () -> {
            User.create("user1@test.com","",createdAt);
        });
        String messagePassw = exceptionPassw.getMessage();
        assertEquals(messagePassw,"La contraseña invalida");

        System.out.println(messagePassw);
    }

    @Test
    @Order(4)
    @DisplayName("only_pending_users_are_activated")

    public void only_pending_users_are_activated(){

        user = User.create("user1@test.com",
                "user1.1234",
                createdAt);

        user.activate();

        BusinessRuleViolationsException exceptionActivate = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    user.activate();
                }
        );

        assertEquals("Solo usuarios pendientes pueden ser activados", exceptionActivate.getMessage());


    }


}
