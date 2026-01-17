package persistence.application.usecase;

import application.dto.response.PendingTaskResponseDTO;
import application.usecase.QueueExportOrdersTask;
import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import infrastructure.persistence.entities.PendingTaskEntity;
import infrastructure.persistence.repository.interfaces.IPendingTaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.example.demo.RestapiApplication.class)
@Transactional
public class QueueExportOrdersTaskIntegrationTest {

    @Autowired
    private QueueExportOrdersTask queueExportOrdersTask;

    @Autowired
    private IPendingTaskRepository jpaRepository;

    @Test
    @DisplayName("IT: Should create export task, persist it in DB and return correct DTO")
    void shouldQueueExportTaskAndPersistIt() {
        // Arrange

        // Act
        PendingTaskResponseDTO response = queueExportOrdersTask.execute();

        // Assert Response
        assertNotNull(response, "La respuesta no debería ser nula");
        assertNotNull(response.id(), "El ID de la tarea no debería ser nulo tras persistir");
        assertEquals(PendingTaskType.EXPORT_ORDERS.toString(), response.type(), "El tipo debe ser EXPORT_ORDERS");
        assertEquals(PendingTaskStatus.PENDING.toString(), response.status(), "El estado inicial debe ser PENDING");

        // Assert Persistence
        Optional<PendingTaskEntity> savedEntityOpt = jpaRepository.findById(response.id());

        assertTrue(savedEntityOpt.isPresent(), "La entidad debería existir en la base de datos");

        PendingTaskEntity savedEntity = savedEntityOpt.get();

        assertEquals(PendingTaskType.EXPORT_ORDERS, savedEntity.getType());
        assertEquals(PendingTaskStatus.PENDING, savedEntity.getStatus());
        assertNotNull(savedEntity.getCreatedAt(), "La fecha de creación debe estar registrada");

        assertTrue(savedEntity.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(savedEntity.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(10)));

        assertNull(savedEntity.getProccessedAt(), "La fecha de procesamiento debe ser nula al inicio");
    }
}