package domain.repository;

import domain.model.PendingTask;

import java.util.List;
import java.util.Optional;

public interface PendingTaskRepository {
    PendingTask save(PendingTask task);
    List<PendingTask> findByStatus(String status);
    Optional<PendingTask> findById(Long id);
}