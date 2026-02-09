package Integration;

import application.dto.request.OrderRequestDTO;
import application.dto.request.UserRequestDTO;
import application.usecase.ActivateUser;
import application.usecase.DownloadGeneratedFiles;
import application.usecase.ProcessPendingTask;
import com.example.demo.RestapiApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RestapiApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class FullUserFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivateUser activateUser;

    @Autowired
    private DownloadGeneratedFiles downloadGeneratedFiles;

    @Autowired
    private ProcessPendingTask processPendingTask;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("E2E: Registro -> Job Activación -> Crear Orden -> Solicitar Export -> Job Exportación -> Descargar")
    void fullUserLifecycleTest() throws Exception {
        // Setup
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String email = "flow-" + uniqueId + "@test.com";
        String password = "securePassword123";

        // Register
        UserRequestDTO registerRequest = new UserRequestDTO(email, password);

        MvcResult registerResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        Long userId = registerJson.get("id").asLong();

        assertEquals("PENDING", registerJson.get("status").asText(), "El usuario debe nacer en PENDING");

        // Activation
        activateUser.execute();

        // (Opcional) Podríamos verificar en DB que ahora es ACTIVE, pero el siguiente paso fallará si no lo es.

        // Create Order
        OrderRequestDTO orderRequest = new OrderRequestDTO(new BigDecimal("150.50"));

        mockMvc.perform(post("/users/" + userId + "/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(150.50))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Request export
        MvcResult exportResult = mockMvc.perform(post("/orders/export/request")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted()) // 202 Accepted
                .andReturn();

        JsonNode exportJson = objectMapper.readTree(exportResult.getResponse().getContentAsString());
        Long taskId = exportJson.get("id").asLong();

        // Task processing
        processPendingTask.execute();

        downloadGeneratedFiles.execute(taskId);

        // Download file
        MvcResult downloadResult = mockMvc.perform(get("/orders/export/" + taskId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andReturn();

        String csvContent = downloadResult.getResponse().getContentAsString();

        assertTrue(csvContent.contains("ID, EMAIL, AMOUNT, STATUS, CREATED_AT"), "Debe tener cabecera");
        assertTrue(csvContent.contains(email), "Debe contener el email del usuario creado");
        assertTrue(csvContent.contains("150.50"), "Debe contener el monto de la orden");
    }
}