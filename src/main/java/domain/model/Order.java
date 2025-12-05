package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private User user;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(Long id, User user, OrderStatus status, BigDecimal amount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory Method para crear una Orden.
     * Regla: Solo usuarios ACTIVOS pueden crear órdenes.
     */
    public static Order create(User user, BigDecimal amount, LocalDateTime createdAt) {
        if (user == null) {
            throw new BusinessRuleViolationsException("La orden debe tener un usuario asociado");
        }
        if (!user.isActive()) {
            throw new BusinessRuleViolationsException("El usuario debe estar ACTIVO para crear órdenes");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationsException("El monto debe ser mayor a 0");
        }

        return new Order(null, user, OrderStatus.PENDING, amount, createdAt, createdAt);
    }

    public void updateStatus(OrderStatus newStatus, LocalDateTime updatedAt) {
        // Aquí se podrían agregar validaciones de transición de estado (ej: no pasar de CANCELLED a APPROVED)
        this.status = newStatus;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}