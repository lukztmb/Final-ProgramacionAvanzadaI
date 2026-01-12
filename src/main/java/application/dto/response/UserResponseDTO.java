package application.dto.response;

public record UserResponseDTO(
        Long id,
        String email,
        String status
) {
}
