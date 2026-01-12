package infrastructure.persistence.repository.implementations;

import domain.model.Order;
import domain.repository.OrderRepository;
import infrastructure.persistence.entities.OrderEntity;
import infrastructure.persistence.mapper.PersistenceMapper; // Asumiendo que existe o se actualiza
import infrastructure.persistence.repository.interfaces.IOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryImp implements OrderRepository {

    private final IOrderRepository jpaRepository;
    private final PersistenceMapper mapper;

    public OrderRepositoryImp(IOrderRepository jpaRepository, PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}