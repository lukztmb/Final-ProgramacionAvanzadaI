package domain.model;

import java.time.LocalDateTime;

public class ActivationToken {
    private String email;
    private String code;
    private LocalDateTime expiresAt;

    public ActivationToken(String email, String code, LocalDateTime expiresAt) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isValid(String inputCode) {
        return this.code.equals(inputCode) && !isExpired();
    }

    public String getEmail() {
        return email;
    }
}
