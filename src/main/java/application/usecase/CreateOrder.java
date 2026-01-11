package application.usecase;

import application.dto.request.OrderRequestDTO;
import application.dto.response.OrderResponseDTO;
import application.mapper.OrderMapper;
import domain.model.Order;
import domain.model.User;
import domain.repository.OrderRepository;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateOrder {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public CreateOrder(OrderRepository orderRepository, UserRepository userRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }
    @Transactional
    public OrderResponseDTO execute(Long userId, OrderRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        if (!user.isActive()) {
            throw new BusinessRuleViolationsException("El usuario debe estar ACTIVO para crear órdenes");
        }

        Order newOrder = Order.create(
                user,
                request.amount(),
                LocalDateTime.now()
        );

        Order savedOrder = orderRepository.save(newOrder);

        return orderMapper.toResponseDTO(savedOrder);
    }
}
