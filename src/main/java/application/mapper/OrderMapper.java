package application.mapper;

import application.dto.response.OrderResponseDTO;
import domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }
        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getAmount(),
                order.getCreatedAt()
        );
    }
}