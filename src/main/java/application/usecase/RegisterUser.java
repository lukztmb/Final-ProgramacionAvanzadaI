package application.usecase;

import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import domain.model.ActivationToken;
import domain.model.User;
import domain.repository.ActivationTokenRepository;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterUser {
    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;

    public RegisterUser(UserRepository userRepository, ActivationTokenRepository activationTokenRepository) {
        this.userRepository = userRepository;
        this.activationTokenRepository = activationTokenRepository;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessRuleViolationsException("Email ya esta registrado");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = User.create(
                request.email(),
                request.password(),
                now
        );

        User savedUser = userRepository.save(user);

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        ActivationToken token = new ActivationToken(
                savedUser.getEmail(),
                code,
                now.plusHours(24)
        );

        activationTokenRepository.save(token);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getStatus().toString()
        );
    }
}