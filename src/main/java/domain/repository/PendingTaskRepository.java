package domain.repository;

import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import java.util.List;
import java.util.Optional;

public interface PendingTaskRepository {
    PendingTask save(PendingTask task);
    Optional<PendingTask> findById(Long id);
    List<PendingTask> findByStatus(PendingTaskStatus status);
}