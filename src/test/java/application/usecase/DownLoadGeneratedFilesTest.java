package application.usecase;

import application.usecase.DownloadGeneratedFiles;
import domain.model.*;
import domain.repository.OrderRepository;
import domain.repository.PendingTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DownLoadGeneratedFilesTest {
    @Mock
    private PendingTaskRepository pendingTaskRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DownloadGeneratedFiles downloadGeneratedFiles;

    @Test
    void shouldProcessPendingTaskSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        PendingTask pendingTask = PendingTask.create(PendingTaskType.EXPORT_ORDERS, now);
        pendingTask.setId(1L);

        when(pendingTaskRepository.findFirstPending()).thenReturn(Optional.of(pendingTask));

        // Creamos un usuario valido para crear ordenes
        User user = User.create("testUser@gmail.com", "pass1234", now);
        user.activate();
        user.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Creamos una orden para poder generar el csv
        Order order = Order.create(user, new BigDecimal("150.00"),now);
        order.setId(1L);

        when(orderRepository.findAll()).thenReturn(List.of(order));

        downloadGeneratedFiles.execute();

        verify(pendingTaskRepository).findFirstPending();
        verify(orderRepository).findAll();

        verify(pendingTaskRepository).save(argThat(t ->
                t.getStatus() == PendingTaskStatus.DONE &&
                t.getProcessedAt() != null));
    }

    @Test
    void shouldDoNothingWhenNoPendingTasksAreFound() {
        when(pendingTaskRepository.findFirstPending()).thenReturn(Optional.empty());

        downloadGeneratedFiles.execute();

        verify(pendingTaskRepository, never()).save(any());
        verify(orderRepository, never()).findAll();
    }

    @Test
    void shouldMarkAsErrorIfExceptionOccurred() {
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());
        task.setId(2L);
        when(pendingTaskRepository.findFirstPending()).thenReturn(Optional.of(task));

        when(orderRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        downloadGeneratedFiles.execute();

        verify(pendingTaskRepository).save(argThat(t ->
                t.getStatus() == PendingTaskStatus.ERROR &&
                t.getProcessedAt() != null));
    }
}
