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

    private User(Long id, String email, String password, UserStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Factory Method para crear un nuevo Usuario.
     * Regla: Los nuevos usuarios comienzan en estado PENDING.
     */
    public static User create(String email, String password, LocalDateTime createdAt) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleViolationsException("El email es requerido");
        }
        if (password == null || password.isBlank() || password.length() < 4) {
            throw new BusinessRuleViolationsException("La contraseña invalida");
        }
        return new User(null, email, password, UserStatus.PENDING, createdAt);
    }

    public void activate() {
        if (this.status != UserStatus.PENDING) {
            throw new BusinessRuleViolationsException("Solo usuarios pendientes pueden ser activados");
        }
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        if (activationExpiresAt == null) {
            this.status = UserStatus.PENDING;
            return false;
        }
        if (activationExpiresAt.isBefore(LocalDateTime.now())) {
            this.status = UserStatus.EXPIRED;
            return false;
        }
        return this.status == UserStatus.ACTIVE;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserStatus getStatus() { return status; }
    public String getActivationCode() { return activationCode; }
    public LocalDateTime getActivationExpiresAt() { return activationExpiresAt; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.activationExpiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setActivateCode(String code) {this.activationCode = code;}
}