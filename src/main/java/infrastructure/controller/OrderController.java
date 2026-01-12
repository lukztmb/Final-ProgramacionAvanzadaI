package infrastructure.controller;

import application.dto.request.OrderRequestDTO;
import application.dto.response.OrderResponseDTO;
import application.usecase.CreateOrder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class OrderController {

    private final CreateOrder createOrder;

    public OrderController(CreateOrder createOrder) {
        this.createOrder = createOrder;
    }

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequestDTO request) {

        OrderResponseDTO response = createOrder.execute(userId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}") // Ojo: la ruta final sería /users/{userId}/orders/{id} conceptualmente
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}