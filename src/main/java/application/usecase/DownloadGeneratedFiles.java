package application.usecase;


import domain.model.Order;
import domain.model.PendingTask;
import domain.repository.OrderRepository;
import domain.repository.PendingTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class DownloadGeneratedFiles {

    private final PendingTaskRepository pendingTaskRepository;
    private final OrderRepository orderRepository;

    private final Path fileStorageLocation = Paths.get("exports").toAbsolutePath().normalize();

    public  DownloadGeneratedFiles(PendingTaskRepository pendingTaskRepository, OrderRepository orderRepository) {
        this.pendingTaskRepository = pendingTaskRepository;
        this.orderRepository = orderRepository;
        initializeStorage();
    }

    private void initializeStorage(){
        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se puedo inicializar la carpeta de almacenamiento", e);
        }
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void execute() {
        Optional<PendingTask> taskPro = pendingTaskRepository.findFirstPending();

        if (taskPro.isEmpty()){
            return;
        }

        PendingTask task = taskPro.get();
        try {
            processTask(task);
        } catch (Exception e){
            System.out.println("Error al procesar el archivo " + e);
            task.markAsError();
            pendingTaskRepository.save(task);
        }
    }

    private void processTask(PendingTask task) throws IOException {
        List<Order> listOrder = orderRepository.findAll();

        StringBuilder csv = new StringBuilder();

        csv.append("ID,USER_EMAIL,AMOUNT,STATUS,CREATED_AT\n");

        for (Order order : listOrder) {
            csv.append(order.getId()).append(",");
            csv.append(order.getUser().getEmail()).append(",");
            csv.append(order.getAmount()).append(",");
            csv.append(order.getStatus()).append(",");
            csv.append(order.getCreatedAt()).append("\n");
        }

        Path target = fileStorageLocation.resolve(task.getId() + ".csv");
        Files.writeString(target, csv.toString());


        task.markAsDone();
        pendingTaskRepository.save(task);
    }
}
