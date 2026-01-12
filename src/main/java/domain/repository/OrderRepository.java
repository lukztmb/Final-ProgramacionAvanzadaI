package domain.repository;

import domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    List<Order> findAll();
}