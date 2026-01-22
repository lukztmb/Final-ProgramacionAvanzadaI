package infrastructure.controller;

import application.dto.request.OrderRequestDTO;
import application.dto.response.OrderResponseDTO;
import application.dto.response.PendingTaskResponseDTO;
import application.usecase.CreateOrder;
import application.usecase.DownloadGeneratedFiles;
import application.usecase.QueueExportOrdersTask;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class OrderController {

    private final CreateOrder createOrder;
    private final QueueExportOrdersTask queueExportOrdersTask;
    private final DownloadGeneratedFiles downloadGeneratedFiles;

    public OrderController(CreateOrder createOrder,
                           QueueExportOrdersTask queueExportOrdersTask,
                           DownloadGeneratedFiles downloadGeneratedFiles) {
        this.createOrder = createOrder;
        this.queueExportOrdersTask = queueExportOrdersTask;
        this.downloadGeneratedFiles = downloadGeneratedFiles;
    }


    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<@NonNull OrderResponseDTO> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequestDTO request) {

        OrderResponseDTO response = createOrder.execute(userId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response); // da 200
    }

    @PostMapping("/orders/export/request")
    public ResponseEntity<@NonNull PendingTaskResponseDTO> requestExport() {
        PendingTaskResponseDTO response = queueExportOrdersTask.execute();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/orders/export/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.accepted().location(location).body(response);
    }

    @GetMapping("/orders/export/{taskId}")
    public ResponseEntity<@NonNull Resource> downloadExport(@PathVariable Long taskId) {
        Resource resource = downloadGeneratedFiles.execute(taskId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"orders_export_" + taskId + ".csv\"")
                .body(resource);
    }
}