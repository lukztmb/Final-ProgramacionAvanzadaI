package application.dto.usecase;

import application.usecase.ProcessPendingTask;
import domain.model.*;
import domain.repository.OrderRepository;
import domain.repository.PendingTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessPendingTaskTest {
    @Mock
    private PendingTaskRepository pendingTaskRepository;

    @Mock
    private OrderRepository orderRepository;

    private ProcessPendingTask processPendingTask;

    @BeforeEach
    void setUp() {
        processPendingTask = new ProcessPendingTask(pendingTaskRepository, orderRepository);
    }

    @Test
    void shouldProcessPendingTaskSuccessfully() throws IOException {
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());
        task.setId(1L);

        // Simulamos que el repositorio devuelve una tarea pendiente
        when(pendingTaskRepository.findByStatus(PendingTaskStatus.PENDING.toString()))
                .thenReturn(List.of(task));

        // Simulamos datos de ordenes
        User user = User.create("test@email.com", "pass123", LocalDateTime.now());
        user.activate();
        user.setExpiresAt(LocalDateTime.now().plusDays(1));

        Order order = Order.create(user, new BigDecimal("100.00"), LocalDateTime.now());
        order.setId(10L);

        when(orderRepository.findAll()).thenReturn(List.of(order));

        processPendingTask.execute();

        // Capturamos el objeto que se guardo para verificar sus cambios
        ArgumentCaptor<PendingTask> taskCaptor = ArgumentCaptor.forClass(PendingTask.class);
        verify(pendingTaskRepository).save(taskCaptor.capture());

        PendingTask savedTask = taskCaptor.getValue();

        // Verificar estado DONE
        assertEquals(PendingTaskStatus.DONE, savedTask.getStatus());
        assertNotNull(savedTask.getProcessedAt());

        //Verificar que guardo un path
        assertNotNull(savedTask.getFileContentPath());
        assertTrue(savedTask.getFileContentPath().endsWith(".csv"));

        // Borrar el archivo generado por el test
        Path generatedFile = Paths.get(savedTask.getFileContentPath());
        Files.deleteIfExists(generatedFile);
    }

    @Test
    void shouldDoNothingWhenNoPendingTasks() {
        when(pendingTaskRepository.findByStatus(PendingTaskStatus.PENDING.toString()))
                .thenReturn(Collections.emptyList());

        processPendingTask.execute();

        verify(orderRepository, never()).findAll();
        verify(pendingTaskRepository, never()).save(any());
    }

    @Test
    void shouldMarkAsErrorIfExceptionOccurs() {
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());
        task.setId(2L);

        when(pendingTaskRepository.findByStatus(PendingTaskStatus.PENDING.toString()))
                .thenReturn(List.of(task));

        // Simulamos error en la DB de ordenes
        when(orderRepository.findAll()).thenThrow(new RuntimeException("Error de conexión"));

        processPendingTask.execute();

        ArgumentCaptor<PendingTask> taskCaptor = ArgumentCaptor.forClass(PendingTask.class);
        verify(pendingTaskRepository).save(taskCaptor.capture());

        PendingTask savedTask = taskCaptor.getValue();
        assertEquals(PendingTaskStatus.ERROR, savedTask.getStatus());
        assertNotNull(savedTask.getProcessedAt());
    }
}
