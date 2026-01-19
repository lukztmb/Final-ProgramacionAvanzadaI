package application.services;

import domain.model.ActivationToken;
import domain.repository.ActivationTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class ActivationTokenServices {
    private final ActivationTokenRepository activationTokenRepository;
    private static final SecureRandom random = new SecureRandom();

    public ActivationTokenServices(ActivationTokenRepository activationTokenRepository) {
        this.activationTokenRepository = activationTokenRepository;
    }

    public String generateActivationToken(String email, LocalDateTime expirationTime) {
        String code = String.format("%06d", random.nextInt(999999));

        ActivationToken activationToken = new ActivationToken(email, code, expirationTime);
        activationTokenRepository.save(activationToken);

        return code;
    }
}
