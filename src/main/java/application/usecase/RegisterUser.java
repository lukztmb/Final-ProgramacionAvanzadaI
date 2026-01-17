package application.usecase;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import application.ports.EmailServices;
import application.services.ActivationTokenServices;
import domain.model.ActivationToken;
import domain.model.User;
import domain.repository.ActivationTokenRepository;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterUser {

    private final UserRepository userRepository;
    private final ActivationTokenServices activationTokenServices;
    private final EmailServices emailServices;

    public  RegisterUser(UserRepository userRepository, ActivationTokenServices activationTokenServices,
                         EmailServices emailServices) {
        this.userRepository = userRepository;
        this.activationTokenServices = activationTokenServices;
        this.emailServices = emailServices;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO request) {
        //Validamos si existe o no el usuario
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessRuleViolationsException("Email ya esta registrado");
        }

        LocalDateTime now = LocalDateTime.now();
        User user = User.create(
                request.email(),
                request.password(),
                now
        );

        //Guardamos el usuario creado
        User savedUser = userRepository.save(user);

        // Generamos el token de activacion
        String code = activationTokenServices.generateActivationToken(savedUser.getEmail(), LocalDateTime.now().plusDays(1));

        savedUser.setActivateCode(code);
        userRepository.save(savedUser);

        // Envio de correo con el codigo de activacion generado
        emailServices.sendActivationCode(savedUser.getEmail(), code);
        //Response del RegisterUser
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getStatus().toString()
        );

    }


}
