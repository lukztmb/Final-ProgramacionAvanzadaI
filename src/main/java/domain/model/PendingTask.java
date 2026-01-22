package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PendingTask {
    private Long id;
    private PendingTaskType type;
    private PendingTaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String fileContentPath;

    private PendingTask(Long id, PendingTaskType type, PendingTaskStatus status, LocalDateTime createdAt,
                        LocalDateTime processedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.fileContentPath = "";
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

    public void markAsDone(String fileContentPath) {
        this.status = PendingTaskStatus.DONE;
        this.processedAt = LocalDateTime.now();
        this.fileContentPath = fileContentPath;
    }

    public void markAsError() {
        this.status = PendingTaskStatus.ERROR;
        this.processedAt = LocalDateTime.now();
    }

    public void setId(Long id) {this.id = id;}

    public void setStatus(PendingTaskStatus status) { this.status = status;}
}
