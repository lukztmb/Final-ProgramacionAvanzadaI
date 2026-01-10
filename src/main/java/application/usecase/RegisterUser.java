package application.usecase;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
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
    private final ActivationTokenRepository tokenRepository;

    public  RegisterUser(UserRepository userRepository, ActivationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO request) {
        //Validamos si existe o no el usuario
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessRuleViolationsException("Email ya esta registrado");
        }

        //Generacion de Token para posterior activacion

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plusHours(24);

        User user = User.create(
                request.email(),
                request.password(),
                token,
                now
        );

        //Guardamos el usuario creado
        User savedUser = userRepository.save(user);

        //Guardamos el token
        ActivationToken tokenSaved = new ActivationToken(request.email(), token, expired);
        tokenRepository.save(tokenSaved);

        //Response del RegisterUser
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getStatus().toString()
        );

    }


}
