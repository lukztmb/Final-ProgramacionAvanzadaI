package Integration;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import application.ports.EmailServices;
import application.usecase.ActivateUser;
import application.usecase.RegisterUser;
import com.example.demo.RestapiApplication;
import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = RestapiApplication.class)
@Transactional // Para que no guarde lo realizado durante el test en la db
public class UserFlowIntegrationTest {
    @Autowired
    private RegisterUser registerUser;

    @Autowired
    private ActivateUser activateUser;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EmailServices emailServices;

    @Test
    void shouldRegisterAndActivateUserSuccessfully() {
        // Simulamos los datos que entran al request
        String email = "joacosuilar@gmal.com";
        String password = "passtest";
        UserRequestDTO request = new UserRequestDTO(email, password);

        // Registramos al usuario
        UserResponseDTO response = registerUser.registerUser(request);

        // Validamos el registro
        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("PENDING", response.status());

        // Validamos que se guardo en la db
        User userPending = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        assertEquals(email, userPending.getEmail());
        assertEquals(UserStatus.PENDING, userPending.getStatus());

        // Interceptamos el codigo (para simulara que se envio y se ingreso)
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailServices).sendActivationCode(emailCaptor.capture(), codeCaptor.capture());

        String captureEmail = emailCaptor.getValue();
        String captureCode = codeCaptor.getValue();

        assertEquals(email, captureEmail);
        assertNotNull(captureCode, "El codigo de activacion no deberia ser nulo");

        // Activacion del usuario
        activateUser.activate(email,  captureCode);

        // Verificamos la activacion del usuario
        User userActivated = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        assertEquals(email, userActivated.getEmail());
        assertEquals(UserStatus.ACTIVE, userActivated.getStatus());
        assertNotNull(userActivated.getActivationExpiresAt(), "Deberia tener fecha de expiracion");
    }

    @Test
    void shouldRegisterAndActivateUserFailActivationCode(){
        // Simulamos los datos que netran al request
        String email = "joacosuilar@gmal.com";
        String password = "passtest";
        UserRequestDTO request = new UserRequestDTO(email, password);

        // Registramos al usuario
        UserResponseDTO response = registerUser.registerUser(request);

        // Validamos el registro
        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("PENDING", response.status());

        // Validamos que se guardo en la db
        User userPending = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        assertEquals(email, userPending.getEmail());
        assertEquals(UserStatus.PENDING, userPending.getStatus());

        // Activacion del usuario con el codigo invalido
        assertThrows(BusinessRuleViolationsException.class, ()->activateUser.activate(email,  "98989898"));

        // Validamos que no se realizo el cambio en el estado
        assertEquals(UserStatus.PENDING, userPending.getStatus());
    }

    @Test
    void shouldRegisterUserFailInvalidData(){
        String email1 = "";
        String password1 = "1235aa";
        UserRequestDTO request1 = new UserRequestDTO(email1, password1);

        String email2 = "jose@test.com";
        String password2 = "1";
        UserRequestDTO request2 = new UserRequestDTO(email2, password2);

        // Intentamos registrar los usuarios
        assertThrows(BusinessRuleViolationsException.class, ()->registerUser.registerUser(request1));
        assertThrows(BusinessRuleViolationsException.class, ()->registerUser.registerUser(request2));

        // Validamos que no se guardaron en la db
        assertThrows(RuntimeException.class, () ->userRepository.findByEmail(email1).orElseThrow(
                                                    () -> new RuntimeException("User not found")));
        assertThrows(RuntimeException.class, () ->userRepository.findByEmail(email2).orElseThrow(
                () -> new RuntimeException("User not found")));

    }
}
