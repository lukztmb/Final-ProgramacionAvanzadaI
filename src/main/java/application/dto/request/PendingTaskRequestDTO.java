package application.dto.request;

import domain.model.OrderStatus;

import java.time.LocalDateTime;

public record PendingTaskRequestDTO(
        LocalDateTime startDate,
        LocalDateTime endDate,
        OrderStatus status
) {}
