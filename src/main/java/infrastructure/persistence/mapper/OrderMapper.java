package infrastructure.persistence.mapper;

import domain.model.Order;
import domain.model.OrderStatus;
import domain.model.User;
import infrastructure.persistence.entities.OrderEntity;
import infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class OrderMapper {

    private final UserMapper userMapper;

    // Inyección de dependencia recomendada en lugar de 'new UserMapper()'
    public OrderMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public OrderEntity toEntity(Order order) {
        if(order == null){return null;}
        OrderEntity entity = new OrderEntity();

        UserEntity userEntity = userMapper.toUserEntity(order.getUser());

        entity.setId(order.getId());
        entity.setUser(userEntity);
        entity.setStatus(order.getStatus());
        entity.setAmount(order.getAmount());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        return entity;
    }

    public Order toDomain(OrderEntity savedEntity) {
        if (savedEntity == null) return null;

        try {

            Constructor<Order> constructor = Order.class.getDeclaredConstructor(
                    Long.class,
                    User.class,
                    OrderStatus.class,
                    BigDecimal.class,
                    LocalDateTime.class,
                    LocalDateTime.class
            );
            constructor.setAccessible(true);

            return constructor.newInstance(
                    savedEntity.getId(),
                    userMapper.toUserDomain(savedEntity.getUser()),
                    savedEntity.getStatus(),
                    savedEntity.getAmount(),
                    savedEntity.getCreatedAt(),
                    savedEntity.getUpdatedAt()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al reconstruir Order desde persistencia", e);
        }
    }
}