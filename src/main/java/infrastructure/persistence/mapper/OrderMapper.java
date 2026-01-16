package infrastructure.persistence.mapper;

import domain.model.Order;
import domain.model.User;
import infrastructure.persistence.entities.OrderEntity;
import infrastructure.persistence.entities.UserEntity;
import infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        if(order == null){return null;}
        OrderEntity entity = new OrderEntity();
        User user = order.getUser();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setStatus(user.getStatus());
        userEntity.setCreatedAt(user.getCreatedAt());
        entity.setId(order.getId());
        entity.setUser(userEntity);
        entity.setStatus(order.getStatus());
        entity.setAmount(order.getAmount());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        return entity;
    }

    public Order toDomain(OrderEntity savedEntity) {
        UserMapper userMapper = new UserMapper();
        return Order.create(
                userMapper.toUserDomain(savedEntity.getUser()),
                savedEntity.getAmount(),
                savedEntity.getCreatedAt()
        );
    }



}
