package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import java.time.LocalDateTime;

public class PendingTask {
    private Long id;
    private PendingTaskType type;
    private PendingTaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    private PendingTask(Long id, PendingTaskType type, PendingTaskStatus status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public static PendingTask create(PendingTaskType type, LocalDateTime createdAt) {
        if (type == null) {
            throw new BusinessRuleViolationsException("El tipo de tarea es requerido");
        }
        if (createdAt == null || createdAt.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleViolationsException("La fecha ingresada es invalida");
        }
        return new PendingTask(null, type, PendingTaskStatus.PENDING, createdAt, null);
    }

    public void markAsDone() {
        this.status = PendingTaskStatus.DONE;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsError() {
        this.status = PendingTaskStatus.ERROR;
        this.processedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PendingTaskType getType() { return type; }
    public PendingTaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
