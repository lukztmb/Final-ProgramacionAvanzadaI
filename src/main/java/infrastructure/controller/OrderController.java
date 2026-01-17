package infrastructure.controller;

import application.dto.request.OrderRequestDTO;
import application.dto.response.OrderResponseDTO;
import application.dto.response.PendingTaskResponseDTO;
import application.usecase.CreateOrder;
import application.usecase.QueueExportOrdersTask;
import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.repository.PendingTaskRepository;
import infrastructure.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class OrderController {

    private final CreateOrder createOrder;
    private final QueueExportOrdersTask queueExportOrdersTask;
    private final PendingTaskRepository pendingTaskRepository;

    public OrderController(CreateOrder createOrder,
                           QueueExportOrdersTask queueExportOrdersTask,
                           PendingTaskRepository pendingTaskRepository) {
        this.createOrder = createOrder;
        this.queueExportOrdersTask = queueExportOrdersTask;
        this.pendingTaskRepository = pendingTaskRepository;
    }


    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequestDTO request) {

        OrderResponseDTO response = createOrder.execute(userId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/orders/export/request")
    public ResponseEntity<PendingTaskResponseDTO> requestExport() {
        PendingTaskResponseDTO response = queueExportOrdersTask.execute();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/orders/export/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.accepted().location(location).body(response);
    }

    @GetMapping("/orders/export/{taskId}")
    public ResponseEntity<Resource> downloadExport(@PathVariable Long taskId) {
        PendingTask task = pendingTaskRepository.findFirstPending()
                .stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);


        try {
            Path filePath = Paths.get("exports").resolve(taskId + ".csv").normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("text/csv"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"orders_export_" + taskId + ".csv\"")
                        .body(resource);
            } else {
                throw new ResourceNotFoundException("El archivo no está listo o no existe.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al leer la ruta del archivo", e);
        }
    }
}