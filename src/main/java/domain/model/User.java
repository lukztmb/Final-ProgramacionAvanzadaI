package domain.model;

import infrastructure.exception.BusinessRuleViolationsException;
import java.time.LocalDateTime;

public class User {
    private Long id;
    private String email;
    private String password;
    private UserStatus status;
    private LocalDateTime createdAt;

    // Constructor privado
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

    // Comportamiento de dominio
    public void activate() {
        if (this.status != UserStatus.PENDING) {
            throw new BusinessRuleViolationsException("Solo usuarios pendientes pueden ser activados");
        }
        this.status = UserStatus.ACTIVE;
    }

    // Getters y Setters necesarios (ID se setea tras persistencia)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}