package model;

import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import infrastructure.exception.BusinessRuleViolationsException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PendingTaskTest {

    private LocalDateTime createdAt;
    private PendingTaskType taskType;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now();
        taskType = PendingTaskType.EXPORT_ORDERS;
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("successful_Task_Creation")
    void successful_Task_Creation() {
        PendingTask task = PendingTask.create(taskType, createdAt);

        assertNotNull(task);

        assertEquals(PendingTaskStatus.PENDING, task.getStatus());
        assertEquals(taskType, task.getType());
        assertNull(task.getProcessedAt());
        assertNull(task.getFileContentPath());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("failure_Task_Creation_Attributes")
    void failure_Task_Creation_Attributes() {
        BusinessRuleViolationsException exceptionType = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    PendingTask.create(null, createdAt);
                });
        assertEquals("El tipo de tarea es requerido", exceptionType.getMessage());

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        BusinessRuleViolationsException exceptionDate = assertThrows(
                BusinessRuleViolationsException.class, () -> {
                    PendingTask.create(taskType, futureDate);
                });
        assertEquals("La fecha ingresada es invalida", exceptionDate.getMessage());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("mark_Task_As_Done")
    void mark_Task_As_Done() {
        PendingTask task = PendingTask.create(taskType, createdAt);
        String fakePath = "/tmp/file.csv";

        task.markAsDone(fakePath);

        assertEquals(PendingTaskStatus.DONE, task.getStatus());
        assertNotNull(task.getProcessedAt());
        assertEquals(fakePath, task.getFileContentPath());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("mark_Task_As_Error")
    void mark_Task_As_Error() {
        PendingTask task = PendingTask.create(taskType, createdAt);

        task.markAsError();

        assertEquals(PendingTaskStatus.ERROR, task.getStatus());
        assertNotNull(task.getProcessedAt());
    }
}