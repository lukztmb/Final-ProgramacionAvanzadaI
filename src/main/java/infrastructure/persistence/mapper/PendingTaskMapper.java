package infrastructure.persistence.mapper;

import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import infrastructure.persistence.entities.PendingTaskEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

@Component
public class PendingTaskMapper {
    public PendingTask toDomain(PendingTaskEntity task) {
        if (task == null) {
            return null;
        }
        try {
            Constructor<PendingTask> constructor = PendingTask.class.getDeclaredConstructor(
                    Long.class,
                    PendingTaskType.class,
                    PendingTaskStatus.class,
                    LocalDateTime.class,
                    LocalDateTime.class,
                    String.class
            );
            constructor.setAccessible(true);

            return constructor.newInstance(
                    task.getId(),
                    task.getType(),
                    task.getStatus(),
                    task.getCreatedAt(),
                    task.getProcessedAt(),
                    task.getFileContentPath()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error en el mapeo PendingTaskMapper.toDomain", e);
        }
    }

    public PendingTaskEntity toEntity(PendingTask task) {
        if (task == null) {
            return null;
        }
        PendingTaskEntity entity = new PendingTaskEntity();
        entity.setId(task.getId());
        entity.setType(task.getType());
        entity.setStatus(task.getStatus());
        entity.setCreatedAt(task.getCreatedAt());
        entity.setProcessedAt(task.getProcessedAt());
        entity.setFileContentPath(task.getFileContentPath());
        return entity;
    }
}