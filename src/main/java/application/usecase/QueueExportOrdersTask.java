package application.usecase;

import application.dto.response.PendingTaskResponseDTO;
import domain.model.PendingTask;
import domain.model.PendingTaskType;
import domain.repository.PendingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class QueueExportOrdersTask {
    private final PendingTaskRepository pendingTaskRepository;

    public QueueExportOrdersTask(PendingTaskRepository pendingTaskRepository) {
        this.pendingTaskRepository = pendingTaskRepository;
    }

    @Transactional
    public PendingTaskResponseDTO execute(){
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());

        PendingTask savedTask = pendingTaskRepository.save(task);

        return new PendingTaskResponseDTO(savedTask.getId(),
        savedTask.getType().toString(),
        savedTask.getStatus().toString());
    }
}
