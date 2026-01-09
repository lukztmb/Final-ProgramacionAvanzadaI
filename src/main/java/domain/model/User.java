package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import java.time.LocalDateTime;

public class User {
    private Long id;
    private String email;
    private String password;
    private UserStatus status;
    private String activationCode;
    private LocalDateTime activationExpiresAt;
    private LocalDateTime createdAt;

    // Constructor privado
    private User(Long id, String email, String password, UserStatus status, String activationCode, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.activationCode = activationCode;   //Usamos un db en memoria
        this.activationExpiresAt = activationExpiresAt;  //el tiempo nos los da la clave de activacion
        this.createdAt = createdAt;
    }

    /**
     * Factory Method para crear un nuevo Usuario.
     * Regla: Los nuevos usuarios comienzan en estado PENDING.
     */
    public static User create(String email, String password, String activationCode, LocalDateTime createdAt) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleViolationsException("El email es requerido");
        }
        if (password == null || password.isBlank() || password.length() < 4) {
            throw new BusinessRuleViolationsException("La contraseña invalida");
        }
        if (activationCode == null || activationCode.isBlank()) {
            throw new BusinessRuleViolationsException("El codigo de activacion es requerido");
        }
        return new User(null, email, password, UserStatus.PENDING, activationCode, createdAt);
    }

    // Comportamiento de dominio
    public void activate() {
        if (this.status != UserStatus.PENDING) {
            throw new BusinessRuleViolationsException("Solo usuarios pendientes pueden ser activados");
        }
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        if (activationExpiresAt.isBefore(LocalDateTime.now())) {
            this.status = UserStatus.EXPIRED;
        }
        return this.status == UserStatus.ACTIVE;
    }


    // Getters y Setters necesarios (ID se setea tras persistencia)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserStatus getStatus() { return status; }
    public String getActivationCode() { return activationCode; }
    public LocalDateTime getActivationExpiresAt() { return activationExpiresAt; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}