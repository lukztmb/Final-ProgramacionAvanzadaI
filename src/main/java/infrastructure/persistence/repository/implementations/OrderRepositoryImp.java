package infrastructure.persistence.repository.implementations;

import domain.model.Order;
import domain.repository.OrderRepository;
import infrastructure.persistence.entities.OrderEntity;
import infrastructure.persistence.mapper.OrderMapper;
import infrastructure.persistence.repository.interfaces.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImp implements OrderRepository {

    private final IOrderRepository jpaRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}