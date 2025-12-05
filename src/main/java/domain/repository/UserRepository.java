package domain.repository;

import domain.model.User;
import domain.model.UserStatus;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
}