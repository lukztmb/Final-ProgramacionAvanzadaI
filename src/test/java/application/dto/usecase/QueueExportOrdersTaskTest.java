package application.dto.usecase;

import application.dto.response.PendingTaskResponseDTO;
import application.usecase.QueueExportOrdersTask;
import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import domain.repository.OrderRepository;
import domain.repository.PendingTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.Notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QueueExportOrdersTaskTest {

    @Mock
    private PendingTaskRepository pendingTaskRepository;

    @InjectMocks
    private QueueExportOrdersTask queueExportOrdersTask;

    @Test
    void shouldCreateExportTaskSuccessfully() {
        when(pendingTaskRepository.save(any(PendingTask.class))).thenAnswer(invocation -> {
            PendingTask task = invocation.getArgument(0);
            task.setId(100L);
            return task;
        });

        PendingTaskResponseDTO response = queueExportOrdersTask.execute();

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(PendingTaskType.EXPORT_ORDERS.toString(), response.type());
        assertEquals(PendingTaskStatus.PENDING.toString(), response.status());

        ArgumentCaptor<PendingTask> captor = ArgumentCaptor.forClass(PendingTask.class);
        verify(pendingTaskRepository).save(captor.capture());

        PendingTask task = captor.getValue();
        assertEquals(PendingTaskType.EXPORT_ORDERS, task.getType());
        assertEquals(PendingTaskStatus.PENDING, task.getStatus());
        assertNotNull(task.getCreatedAt());
    }
}
