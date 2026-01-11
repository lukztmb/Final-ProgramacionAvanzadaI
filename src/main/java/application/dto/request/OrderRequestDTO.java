package application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record OrderRequestDTO(
        @NotNull(message = "El monto es requerido")
        @Positive(message = "El monto debe ser mayor a 0")
        BigDecimal amount
) {}