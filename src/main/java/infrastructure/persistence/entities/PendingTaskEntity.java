package infrastructure.persistence.entities;

import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_tasks")
public class PendingTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PendingTaskType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PendingTaskStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime proccessedAt;

    public PendingTaskEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PendingTaskType getType() {
        return type;
    }

    public void setType(PendingTaskType type) {
        this.type = type;
    }

    public PendingTaskStatus getStatus() {
        return status;
    }

    public void setStatus(PendingTaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProccessedAt() {
        return proccessedAt;
    }

    public void setProccessedAt(LocalDateTime proccessedAt) {
        this.proccessedAt = proccessedAt;
    }
}
