package usecase;

import application.dto.request.OrderRequestDTO;
import application.dto.response.OrderResponseDTO;
import application.mapper.OrderMapper;
import application.usecase.CreateOrder;
import domain.model.Order;
import domain.model.OrderStatus;
import domain.model.User;
import domain.repository.OrderRepository;
import domain.repository.UserRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CreateOrder createOrder;

    private User activeUser;
    private User pendingUser;

    @BeforeEach
    void setUp() {
        activeUser = User.create("active@test.com", "pass123", "code1", LocalDateTime.now());
        activeUser.setId(1L);
        activeUser.activate();

        pendingUser = User.create("pending@test.com", "pass123", "code2", LocalDateTime.now());
        pendingUser.setId(2L);
    }

    @Test
    @DisplayName("Happy Path: Debería crear orden correctamente cuando usuario es activo y monto válido")
    void shouldCreateOrder_WhenUserIsActiveAndAmountIsValid() {
        // Arrange
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.50");
        OrderRequestDTO request = new OrderRequestDTO(amount);

        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(10L); // Simulamos ID generado por DB
            return savedOrder;
        });

        OrderResponseDTO expectedResponse = new OrderResponseDTO(
                10L, userId, OrderStatus.PENDING, amount, LocalDateTime.now()
        );
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(expectedResponse);

        // Act
        OrderResponseDTO actualResponse = createOrder.execute(userId, request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(10L, actualResponse.id());
        assertEquals(userId, actualResponse.userId());
        assertEquals(amount, actualResponse.amount());

        verify(userRepository).findById(userId);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toResponseDTO(any(Order.class));
    }

    @Test
    @DisplayName("Unhappy Flow: Debería lanzar ResourceNotFoundException cuando usuario no existe")
    void shouldThrowException_WhenUserNotFound() {
        // Arrange
        Long nonExistentUserId = 99L;
        OrderRequestDTO request = new OrderRequestDTO(BigDecimal.TEN);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                createOrder.execute(nonExistentUserId, request)
        );

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unhappy Flow: Debería lanzar BusinessRuleViolationsException cuando usuario NO está activo")
    void shouldThrowException_WhenUserIsNotActive() {
        // Arrange
        Long pendingUserId = 2L;
        OrderRequestDTO request = new OrderRequestDTO(BigDecimal.TEN);

        when(userRepository.findById(pendingUserId)).thenReturn(Optional.of(pendingUser));

        // Act & Assert
        BusinessRuleViolationsException exception = assertThrows(BusinessRuleViolationsException.class, () ->
                createOrder.execute(pendingUserId, request)
        );

        assertEquals("El usuario debe estar ACTIVO para crear órdenes", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unhappy Flow: Debería lanzar BusinessRuleViolationsException cuando monto es inválido (<= 0)")
    void shouldThrowException_WhenAmountIsInvalid() {
        // Arrange
        Long userId = 1L;
        BigDecimal invalidAmount = BigDecimal.ZERO; // O negativo
        OrderRequestDTO request = new OrderRequestDTO(invalidAmount);

        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        // Act & Assert
        BusinessRuleViolationsException exception = assertThrows(BusinessRuleViolationsException.class, () ->
                createOrder.execute(userId, request)
        );

        assertEquals("El monto debe ser mayor a 0", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }
}