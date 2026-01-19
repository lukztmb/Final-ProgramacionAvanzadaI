package Integration;

import application.dto.request.ActivateUserRequestDTO;
import application.dto.request.OrderRequestDTO;
import application.dto.request.UserRequestDTO;
import application.usecase.DownloadGeneratedFiles;
import com.example.demo.RestapiApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.model.ActivationToken;
import domain.repository.ActivationTokenRepository;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RestapiApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class FullUserFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private DownloadGeneratedFiles downloadGeneratedFiles;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("Flujo Completo: Registro -> Activación -> Orden -> Exportación -> Descarga")
    void fullUserLifecycleTest() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        //String email = "test-" + uniqueId + "@cleanflow.com";
        String email = "joacosuilar@gmail.com";
        String password = "securePassword123";

        // REGISTRO DE USUARIO
        UserRequestDTO registerRequest = new UserRequestDTO(email, password);

        MvcResult registerResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponseStr = registerResult.getResponse().getContentAsString();
        JsonNode registerJson = objectMapper.readTree(registerResponseStr);

        Long userId = registerJson.get("id").asLong();
        String userStatus = registerJson.get("status").asText();

        assertEquals("PENDING", userStatus);
        assertNotNull(userId);

        // OBTENCIÓN DEL TOKEN (Simulación Backdoor)
        Optional<ActivationToken> tokenOpt = activationTokenRepository.findByEmail(email);
        assertTrue(tokenOpt.isPresent(), "El token debería haberse generado al registrar el usuario");

        String tokenCode = getPrivateCode(tokenOpt.get());

        // ACTIVACIÓN DE USUARIO
        ActivateUserRequestDTO activateRequest = new ActivateUserRequestDTO(email, tokenCode);

        mockMvc.perform(post("/users/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activateRequest)))
                .andExpect(status().isNoContent());

        // CREACIÓN DE ORDEN
        OrderRequestDTO orderRequest = new OrderRequestDTO(new BigDecimal("150.50"));

        mockMvc.perform(post("/users/" + userId + "/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"amount\":150.50,\"status\":\"PENDING\"}"));

        // SOLICITUD DE EXPORTACIÓN (Generar Tarea)
        MvcResult exportResult = mockMvc.perform(post("/orders/export/request")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();

        String exportResponseStr = exportResult.getResponse().getContentAsString();
        JsonNode exportJson = objectMapper.readTree(exportResponseStr);
        Long taskId = exportJson.get("id").asLong();

        // EJECUCIÓN MANUAL DEL JOB
        downloadGeneratedFiles.execute();

        // DESCARGA DEL ARCHIVO GENERADO
        MvcResult downloadResult = mockMvc.perform(get("/orders/export/" + taskId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andReturn();

        String csvContent = downloadResult.getResponse().getContentAsString();

        assertTrue(csvContent.contains("ID,USER_EMAIL,AMOUNT"), "El CSV debe contener la cabecera");
        assertTrue(csvContent.contains(email), "El CSV debe contener el email dinámico del usuario");
        assertTrue(csvContent.contains("150.50"), "El CSV debe contener el monto");
    }

    private String getPrivateCode(ActivationToken token) throws Exception {
        Field field = ActivationToken.class.getDeclaredField("code");
        field.setAccessible(true);
        return (String) field.get(token);
    }
}