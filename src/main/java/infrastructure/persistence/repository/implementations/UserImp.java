package infrastructure.persistence.repository.implementations;

import domain.model.User;
import domain.model.UserStatus;
import domain.repository.UserRepository;
import infrastructure.persistence.entities.UserEntity;
import infrastructure.persistence.mapper.UserMapper;
import infrastructure.persistence.repository.interfaces.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserImp implements UserRepository {
    private final IUserRepository repository;
    private final UserMapper mapper;


    @Override
    public User save(User user) {
        UserEntity entity = mapper.toUserEntity(user);
        UserEntity saveEntity = repository.save(entity);
        return mapper.toUserDomain(saveEntity);

    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(mapper::toUserDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toUserDomain);
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return repository.findAllByType(status)
                .stream()
                .map(mapper::toUserDomain)
                .collect(Collectors.toList());
    }
}
