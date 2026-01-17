package domain.repository;

import domain.model.PendingTask;
import java.util.Optional;

public interface PendingTaskRepository {
    PendingTask save(PendingTask task);
    Optional<PendingTask> findFirstPending();
    Optional<PendingTask> findById(Long id);
}