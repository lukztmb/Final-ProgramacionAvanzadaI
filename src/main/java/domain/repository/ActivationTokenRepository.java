package domain.repository;

import domain.model.ActivationToken;

import java.util.Optional;

public interface ActivationTokenRepository {
    void save(ActivationToken token);
    Optional<ActivationToken> findByEmail(String email);
    void delete(String email);
}
