package infrastructure.persistence.mapper;

import domain.model.User;
import domain.model.UserStatus;
import infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toUserDomain(UserEntity entity) {
        if (entity == null) return null;
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor(
                    Long.class,
                    String.class,
                    String.class,
                    UserStatus.class,
                    LocalDateTime.class
            );
            constructor.setAccessible(true);
            User user = constructor.newInstance(
                    entity.getId(),
                    entity.getEmail(),
                    entity.getPassword(),
                    entity.getStatus(),
                    entity.getCreatedAt()
            );

            user.setActivationCode(entity.getActivationCode());
            user.setExpiresAt(entity.getActivationExpiresAt());

            return user;
        }catch (Exception e){
            throw new RuntimeException("Error Reconstruyendo el User desde la Persistencia",e);
        }
    }

    public UserEntity toUserEntity(User user) {
        if (user == null) return null;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setStatus(user.getStatus());
        userEntity.setCreatedAt(user.getCreatedAt());

        userEntity.setActivationCode(user.getActivationCode() != null ? user.getActivationCode() : "");

        userEntity.setExpiresAt(user.getActivationExpiresAt() != null ? user.getActivationExpiresAt() : LocalDateTime.now().plusDays(1));

        return userEntity;
    }

}