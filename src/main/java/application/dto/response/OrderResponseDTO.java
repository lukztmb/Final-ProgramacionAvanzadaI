package application.dto.response;

import domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDTO(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal amount,
        LocalDateTime createdAt
) {}