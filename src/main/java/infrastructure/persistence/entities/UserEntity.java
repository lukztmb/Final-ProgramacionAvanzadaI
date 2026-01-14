package infrastructure.persistence.entities;

import domain.model.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users_table")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private String activationCode;

    @Column(nullable = false)
    private LocalDateTime activationExpiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders = new ArrayList<OrderEntity>();

    public UserEntity() {}

    public void addOrders(OrderEntity order){
        orders.add(order);
        order.setUser(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserStatus getStatus() { return status; }
    public String getActivationCode() { return activationCode; }
    public LocalDateTime getActivationExpiresAt() { return activationExpiresAt; }
    public void serActivationCode(String activationCode) { this.activationCode = activationCode; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.activationExpiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderEntity> getOrders (){return orders;}
    public void setOrders(List<OrderEntity> orders) {this.orders = orders;}


}
