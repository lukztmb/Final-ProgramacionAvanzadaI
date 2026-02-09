package application.usecase;

import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivateUser {
    private final UserRepository userRepository;

    public ActivateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    @Transactional
    public void execute() {

        List<User> pendingUsers = userRepository.findByStatus(UserStatus.PENDING);

        pendingUsers.forEach(user -> {
            user.activate();
            userRepository.save(user);
            System.out.println("User activated successfully (" + user.getEmail() +")");
        });
    }
}
