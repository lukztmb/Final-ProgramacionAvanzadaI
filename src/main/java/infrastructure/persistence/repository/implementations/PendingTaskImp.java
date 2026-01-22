package infrastructure.persistence.repository.implementations;

import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.repository.PendingTaskRepository;
import infrastructure.persistence.entities.PendingTaskEntity;
import infrastructure.persistence.mapper.PendingTaskMapper;
import infrastructure.persistence.repository.interfaces.IPendingTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
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
    public Optional<PendingTask> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PendingTask> findByStatus(PendingTaskStatus status) {
        return repository.findByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
