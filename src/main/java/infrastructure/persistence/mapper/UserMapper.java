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
            return constructor.newInstance(
                    entity.getId(),
                    entity.getEmail(),
                    entity.getPassword(),
                    entity.getStatus(),
                    entity.getCreatedAt()
            );
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
        return userEntity;
    }

}
