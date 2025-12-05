package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import java.time.LocalDateTime;

public class PendingTask {
    private Long id;
    private PendingTaskStatus type;
    private PendingTaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    private PendingTask(Long id, PendingTaskStatus type, PendingTaskStatus status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public static PendingTask create(PendingTaskStatus type, LocalDateTime createdAt) {
        if (type == null) {
            throw new BusinessRuleViolationsException("El tipo de tarea es requerido");
        }
        return new PendingTask(null, type, PendingTaskStatus.PENDING, createdAt, null);
    }

    public void markAsDone(LocalDateTime processedAt) {
        this.status = PendingTaskStatus.DONE;
        this.processedAt = processedAt;
    }

    public void markAsError(LocalDateTime processedAt) {
        this.status = PendingTaskStatus.ERROR;
        this.processedAt = processedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PendingTaskStatus getType() { return type; }
    public PendingTaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}