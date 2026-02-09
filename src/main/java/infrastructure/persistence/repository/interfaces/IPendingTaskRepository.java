package infrastructure.persistence.repository.interfaces;

import domain.model.PendingTaskStatus;
import infrastructure.persistence.entities.PendingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPendingTaskRepository extends JpaRepository<PendingTaskEntity, Long> {
    List<PendingTaskEntity> findByStatus(PendingTaskStatus status);
}
