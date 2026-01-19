package application.usecase;

import domain.model.Order;
import domain.model.PendingTask;
import domain.model.PendingTaskStatus;
import domain.model.PendingTaskType;
import domain.repository.OrderRepository;
import domain.repository.PendingTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProcessPendingTask {
    private final PendingTaskRepository pendingTaskRepository;
    private final OrderRepository orderRepository;
    private final Path filePath;

    public  ProcessPendingTask(PendingTaskRepository pendingTaskRepository, OrderRepository orderRepository) {
        this.pendingTaskRepository = pendingTaskRepository;
        this.orderRepository = orderRepository;

        this.filePath = Paths.get("generated-reports").toAbsolutePath().normalize();
        try{
            Files.createDirectories(filePath);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear el directoria para el almacenamiento");
        }
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void execute() {
        List<PendingTask> tasks = pendingTaskRepository.findByStatus(PendingTaskStatus.PENDING.toString());

        for (PendingTask task : tasks) {
            if (task.getType() == PendingTaskType.EXPORT_ORDERS) {
                processTask(task);
            }
        }
    }

    private void processTask(PendingTask task) {
        try {
            List<Order> orders = orderRepository.findAll();

            String csvContent = generateCsv(orders);

            String fileName = task.getId() + ".csv";
            Path targetLocation = this.filePath.resolve(fileName);

            Files.write(targetLocation, csvContent.getBytes());

            task.markAsDone(targetLocation.toString());
            pendingTaskRepository.save(task);
        }catch (Exception e){
            e.printStackTrace();
            task.markAsError();
            pendingTaskRepository.save(task);
        }
    }

    private String generateCsv(List<Order> orders) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID, EMAIL, AMOUNT, STATUS, CREATED_AT\n");

        for (Order order : orders) {
            csv.append(order.getId()).append(", ");
            csv.append(order.getUser().getEmail()).append(", ");
            csv.append(order.getAmount()).append(", ");
            csv.append(order.getStatus()).append(", ");
            csv.append(order.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }
}
