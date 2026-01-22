package model;

import domain.model.OrderStatus;
import domain.model.User;
import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private User activeUser;
    private User inactiveUser;
    private LocalDateTime createdAt;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now();
        amount = new BigDecimal("15000");

        activeUser = User.create("test@test.com", "pass.1234", createdAt);
        activeUser.activate();

        inactiveUser = User.create("test2@test.com", "pass.1234", createdAt);
    }

    @Test
    @Order(1)
    @DisplayName("Create_order_success")
    public void create_Order_Success() {
        domain.model.Order order = domain.model.Order.create(activeUser, amount, createdAt);

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(activeUser, order.getUser());
        assertEquals(amount, order.getAmount());
    }

    @Test
    @Order(2)
    @DisplayName("failure_When_User_Is_Not_Active")
    public void failure_When_User_Is_Not_Active() {
        BusinessRuleViolationsException exception = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    domain.model.Order.create(inactiveUser, amount, createdAt);
                });
        assertEquals("El usuario debe estar ACTIVO para crear órdenes", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("failure_When_Amount_Is_Invalid")
    void failure_When_Amount_Is_Invalid() {
        BusinessRuleViolationsException exceptionNull = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    domain.model.Order.create(activeUser, null, createdAt);
                });
        assertEquals("El monto debe ser mayor a 0", exceptionNull.getMessage());

        BusinessRuleViolationsException exceptionZero = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    domain.model.Order.create(activeUser, BigDecimal.ZERO, createdAt);
                });
        assertEquals("El monto debe ser mayor a 0", exceptionZero.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("failure_When_Date_Is_Invalid")
    void failure_When_Date_Is_Invalid() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        BusinessRuleViolationsException exception = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    domain.model.Order.create(activeUser, new BigDecimal("50.0"), futureDate);
                });

        assertEquals("La fecha ingresada es invalida", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("update_Order_Status")
    void update_Order_Status() {
        domain.model.Order order = domain.model.Order.create(activeUser, new BigDecimal("100.00"), createdAt);

        order.updateStatus(OrderStatus.APPROVED);

        assertEquals(OrderStatus.APPROVED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
    }
}