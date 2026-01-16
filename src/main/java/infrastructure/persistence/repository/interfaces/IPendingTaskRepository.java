package infrastructure.persistence.repository.interfaces;

import domain.model.PendingTaskStatus;
import infrastructure.persistence.entities.PendingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPendingTaskRepository extends JpaRepository<PendingTaskEntity, Long> {
    Optional<PendingTaskEntity> findFirstByStatus(PendingTaskStatus status);
}
