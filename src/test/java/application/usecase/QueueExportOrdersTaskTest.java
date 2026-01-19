package application.usecase;

import application.dto.response.PendingTaskResponseDTO;
import application.usecase.QueueExportOrdersTask;
import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import domain.repository.PendingTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        // Simulamos la respuesta
        when(pendingTaskRepository.save(any(PendingTask.class))).thenAnswer(invocation -> {
            PendingTask task = invocation.getArgument(0);
            task.setId(100L);
            return task;
        });

        // Ekecutamos el caso de uso
        PendingTaskResponseDTO response = queueExportOrdersTask.execute();

        //validamos
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

    @Test
    void shouldCreateExportTaskFailureIfExceptionOccurred() {
        // Simulamos que el repositorio lanza una excepcion
        String errorMessage = "Database error";
        when(pendingTaskRepository.save(any(PendingTask.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // Verificamos que al ejecutar el caso de uso, la excepcion se propague
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            queueExportOrdersTask.execute();
        });

        // Verificamos el mensaje y si se llamo al metodo
        assertEquals(errorMessage, exception.getMessage());
        verify(pendingTaskRepository).save(any(PendingTask.class));
    }
}
