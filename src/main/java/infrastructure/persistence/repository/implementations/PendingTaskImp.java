package infrastructure.persistence.repository.implementations;

import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.repository.PendingTaskRepository;
import infrastructure.persistence.entities.PendingTaskEntity;
import infrastructure.persistence.mapper.PendingTaskMapper;
import infrastructure.persistence.repository.interfaces.IPendingTaskRepository;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PendingTaskImp implements PendingTaskRepository {

    private final IPendingTaskRepository repository;
    private final PendingTaskMapper mapper;

    @Override
    public PendingTask save(PendingTask task) {
        PendingTaskEntity entity = mapper.toEntity(task);

        PendingTaskEntity saveEntity = repository.save(entity);

        return mapper.toDomain(saveEntity);
    }

    @Override
    public Optional<PendingTask> findFirstPending() {
        return repository.findFirstByStatus(PendingTaskStatus.PENDING).map(mapper::toDomain);
    }
}
