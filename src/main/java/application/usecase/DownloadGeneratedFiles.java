package application.usecase;

import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.repository.PendingTaskRepository;
import infrastructure.exception.BusinessRuleViolationsException;
import infrastructure.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DownloadGeneratedFiles {
    private final PendingTaskRepository pendingTaskRepository;

    public DownloadGeneratedFiles(PendingTaskRepository pendingTaskRepository) {
        this.pendingTaskRepository = pendingTaskRepository;
    }

    public Resource execute (Long taskId) {
        PendingTask task = pendingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        if (task.getStatus() != PendingTaskStatus.DONE) {
            throw new ResourceNotFoundException("La tarea todavia no ha finalizado");
        }

        try {
            Path filePath = Paths.get(task.getFileContentPath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("El archivo no existe");
            }
        } catch (MalformedURLException e){
            throw new BusinessRuleViolationsException("Error en leer la ruta del archivo");
        }
    }
}
