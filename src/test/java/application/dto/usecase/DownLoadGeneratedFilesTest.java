package application.dto.usecase;

import application.usecase.DownloadGeneratedFiles;
import application.usecase.ProcessPendingTask;
import domain.model.*;
import domain.repository.OrderRepository;
import domain.repository.PendingTaskRepository;
import infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DownLoadGeneratedFilesTest {
    @Mock
    private PendingTaskRepository pendingTaskRepository;

    @InjectMocks
    private DownloadGeneratedFiles downloadGeneratedFiles;

    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test_report", ".csv");
        Files.writeString(tempFile, "CONTENIDO, DE, PRUEBA");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Borramos el archivo temporal
        Files.deleteIfExists(tempFile);
    }
    @Test
    void shouldReturnResourceWhenTaskIsDoneAndFileExists() throws IOException {
        Long taskId = 1L;
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());

        // Forzamos estado DONE y seteamos el path del archivo temporal creado
        task.markAsDone(tempFile.toAbsolutePath().toString());

        when(pendingTaskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Resource resource = downloadGeneratedFiles.execute(taskId);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        Long taskId = 99L;
        when(pendingTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                downloadGeneratedFiles.execute(taskId)
        );
    }

    @Test
    void shouldThrowExceptionWhenTaskIsNotDone() {
        Long taskId = 1L;
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());
        // El estado inicial es PENDING

        when(pendingTaskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                downloadGeneratedFiles.execute(taskId)
        );
        assertEquals("La tarea todavia no ha finalizado", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhysicalFileDoesNotExist() {
        Long taskId = 1L;
        PendingTask task = PendingTask.create(PendingTaskType.EXPORT_ORDERS, LocalDateTime.now());

        // Seteamos una ruta falsa que no existe
        task.markAsDone("Ruta/Inexistente/archivo.csv");

        when(pendingTaskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                downloadGeneratedFiles.execute(taskId)
        );
        assertEquals("El archivo no existe", exception.getMessage());
    }
}
