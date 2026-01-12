package application.dto.response;

public record PendingTaskResponseDTO(Long id,
                                     String type,
                                     String status) {
}
