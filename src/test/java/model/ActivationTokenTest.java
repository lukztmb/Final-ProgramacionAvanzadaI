package model;

import domain.model.ActivationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ActivationTokenTest {

    @Test
    @DisplayName("Valid if the code matches and has not expired")
    void validToken() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(10);
        ActivationToken token = new ActivationToken("test@mail.com", "1234", future);

        assertTrue(token.isValid("1234"));
    }

    @Test
    @DisplayName("It should be invalid if the code does not match")
    void invalidCode() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(10);
        ActivationToken token = new ActivationToken("test@mail.com", "1234", future);

        assertFalse(token.isValid("9999"));
    }

    @Test
    @DisplayName("It should be expired if the date has already passed")
    void expiredToken() {
        LocalDateTime past = LocalDateTime.now().minusMinutes(1);
        ActivationToken token = new ActivationToken("test@mail.com", "1234", past);

        assertTrue(token.isExpired());
        assertFalse(token.isValid("1234"));
    }
}