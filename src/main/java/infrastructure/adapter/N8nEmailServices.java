package infrastructure.adapter;

import application.ports.EmailServices;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class N8nEmailServices implements EmailServices {

    private final String WEBHOOK_URL = "https://j-s-m-code.app.n8n.cloud/webhook/activar-usuario";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendActivationCode(String email, String activationCode) {
        Map<String, String> payLoad = Map.of("email", email, "activationCode", activationCode);

        try{
            restTemplate.postForEntity(WEBHOOK_URL, payLoad, String.class);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
