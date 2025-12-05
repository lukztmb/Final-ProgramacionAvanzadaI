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
    private User(Long id, String email, String password, UserStatus status, String activationCode, LocalDateTime activationExpiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.activationCode = activationCode;
        this.activationExpiresAt = activationExpiresAt;
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
        if (password == null || password.isBlank()) {
            throw new BusinessRuleViolationsException("La contraseña es requerida");
        }

        // Se asume expiración en 24hs por defecto (ajustable según regla de negocio específica)
        LocalDateTime expiresAt = createdAt.plusHours(24);

        return new User(null, email, password, UserStatus.PENDING, activationCode, expiresAt, createdAt);
    }

    // Comportamiento de dominio
    public void activate() {
        if (this.status != UserStatus.PENDING) {
            throw new BusinessRuleViolationsException("Solo usuarios pendientes pueden ser activados");
        }
        this.status = UserStatus.ACTIVE;
        this.activationCode = null; // Limpiar código tras uso
    }

    public boolean isActive() {
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
    public LocalDateTime getCreatedAt() { return createdAt; }
}