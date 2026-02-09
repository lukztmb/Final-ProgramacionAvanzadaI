package application.usecase;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import domain.model.User;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterUser {
    private final UserRepository userRepository;

    public  RegisterUser(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        //Response del RegisterUser
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getStatus().toString()
        );

    }


}
