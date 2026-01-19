package application.ports;


public interface EmailServices {
    void sendActivationCode(String email, String activationCode);
}
