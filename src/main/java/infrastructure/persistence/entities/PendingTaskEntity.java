package infrastructure.persistence.entities;

import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
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

}
