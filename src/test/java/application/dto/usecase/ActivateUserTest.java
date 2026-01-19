package application.dto.usecase;


import application.usecase.ActivateUser;
import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivateUserTest {

    private UserRepository userRepository;
    private ActivationTokenRepository tokenRepository;
    private ActivateUser activateUser;

    @BeforeEach
    void setUp() {
        // Db de usuarios
        userRepository = mock(UserRepository.class);

        // Implementacion para los token
        tokenRepository = new InMemoryActivationTokenRepository();

        // Iniciamos el CU
        activateUser = new ActivateUser(userRepository, tokenRepository);
    }

    @Test
    void shouldActivateUserSuccessfully() {
        // Preparamos
        String email = "esto@test.com";
        String validCode = "123456";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusHours(24);

        /*
            Creamos manuelmente a un usuario para simular que se registro
            Colocamos el estado en pendiente de su activacion
         */
        User pendigUser = User.create(email, "pass1234", now);

        //Simulamos que fue encontrado
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(pendigUser));

        // Guardamos un token manualmente
        ActivationToken activationToken = new ActivationToken(email, validCode, expiredAt);
        tokenRepository.save(activationToken);

        // Ejecutamos
        activateUser.activate(email, validCode);

        // Verificamos
        // validamos si el estado del usuario esta en activado
        assertEquals(UserStatus.ACTIVE, pendigUser.getStatus());

        // Verificamos si se llamo al save
        verify(userRepository).save(pendigUser);

        // Verificamos si el token se borro despues de usarse
        assertTrue(tokenRepository.findByEmail(email).isEmpty(), "El token no deberia existir");
    }

    @Test
    void shouldNotActivateUserIfCodeIsInvalid() {
        // Preparamos
        String email = "esto@test.com";
        String validCode = "123456";
        String wrongCode = "999999";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusHours(24);

        // creamos el usuario valido
        User pendigUser = User.create(email, "pass1234", now);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(pendigUser));

        // creamos el token
        ActivationToken activationToken = new ActivationToken(email, validCode, expiredAt);
        tokenRepository.save(activationToken);

        // ejecutamos y validamos
        assertThrows(BusinessRuleViolationsException.class, () -> activateUser.activate(email, wrongCode));

        // verificamos que el usuer no cambie de estado
        assertEquals(UserStatus.PENDING, pendigUser.getStatus());
        // El token no debe borrarse
        assertFalse(tokenRepository.findByEmail(email).isEmpty());
    }
}
