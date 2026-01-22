package application.usecase;

import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateUser {
    private final UserRepository userRepository;

    public ActivateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void activate(String email) {
        // Buscamos el usuario
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessRuleViolationsException("El usuario no existe"));

        // Activamos el usuario
        if(user.getStatus() == UserStatus.PENDING){
            user.activate();
        }
        //Guardamos los cambios
        userRepository.save(user);
    }
}
