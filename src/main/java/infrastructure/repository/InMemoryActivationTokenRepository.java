package infrastructure.repository;

import domain.model.ActivationToken;
import domain.repository.ActivationTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryActivationTokenRepository implements ActivationTokenRepository {

    private Map<String, ActivationToken> tokens = new ConcurrentHashMap<>();

    @Override
    public void save(ActivationToken token) {
        tokens.put(token.getEmail(), token);
    }

    @Override
    public Optional<ActivationToken> findByEmail(String email) {
        return Optional.ofNullable(tokens.get(email));
    }

    @Override
    public void delete(String email) {
        tokens.remove(email);
        tokens.remove(email);
    }
}
