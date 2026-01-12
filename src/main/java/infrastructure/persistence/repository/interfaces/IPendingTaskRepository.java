package infrastructure.persistence.repository.interfaces;

import infrastructure.persistence.entities.PendingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPendingTaskRepository extends JpaRepository<PendingTaskEntity, Long> {
    Optional<PendingTaskEntity> findByPending();
}
