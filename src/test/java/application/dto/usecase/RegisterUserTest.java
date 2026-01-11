package application.dto.usecase;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import application.usecase.RegisterUser;
import domain.repository.ActivationTokenRepository;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.repository.InMemoryActivationTokenRepository;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegisterUserTest {

    private UserRepository userRepository;
    private RegisterUser registerUser;
    private ActivationTokenRepository tokenRepository;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);
        tokenRepository = mock(InMemoryActivationTokenRepository.class);
        registerUser = new RegisterUser(userRepository);
    }

    @Test
    @Order(1)
    @DisplayName("Register_Use")

    void shouldRegisterUserSuccessfully(){
        UserRequestDTO request = new UserRequestDTO("ejemplo@test.com",
                "passwExample");

        //simulamos que existe este email no existe
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        //simulamos el guardado y retornamos el mismo usuario con id seteado
        when(userRepository.save(any())).thenAnswer(i -> {
            domain.model.User u =i.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponseDTO responseDTO = registerUser.registerUser(request);

        assertNotNull(responseDTO);
        assertEquals("ejemplo@test.com",responseDTO.email());
        assertEquals("PENDING", responseDTO.status());

        verify(userRepository, times(1)).save(any());

    }

    @Test
    @Order(2)
    @DisplayName("Email_User_Exist")
    void shouldEmailUserExist(){
        UserRequestDTO request = new UserRequestDTO("exists@test.com", "passw123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(domain.model.User.class)));

        BusinessRuleViolationsException exception = assertThrows(
                BusinessRuleViolationsException.class,
                () -> registerUser.registerUser(request)
        );

        assertEquals("Email ya esta registrado", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @Order(3)
    @DisplayName("Passw_Short")
    void shouldFailIfPasswordIsTooShort() {
        UserRequestDTO request = new UserRequestDTO("short@test.com", "123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessRuleViolationsException.class, () -> registerUser.registerUser(request));
    }

}


