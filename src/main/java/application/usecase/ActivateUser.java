package application.usecase;

import domain.model.ActivationToken;
import domain.model.User;
import domain.repository.ActivationTokenRepository;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateUser {
    private final UserRepository userRepository;
    private final ActivationTokenRepository tokenRepository;

    public ActivateUser(UserRepository userRepository, ActivationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void activate(String email, String inputCode) {
        // Buscamos el usuario
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessRuleViolationsException("El usuario no existe"));

        // Buscamos el codigo en la db en memoria
        ActivationToken token = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleViolationsException("No existe el token para el usuario"));

        // Validamos el token y su expiracion
        if (!token.isValid(inputCode)) {
            if (token.isExpired()){
                throw new BusinessRuleViolationsException("El codigo ya expiro");
            }
            throw new BusinessRuleViolationsException("Codigo de activacion invalido");
        }

        // Activamos el usuario
        user.activate();
        //Guardamos los cambios
        userRepository.save(user);
        // limpiamos
        tokenRepository.delete(email);
    }
}
