package essay.essay.Models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payerEmail;
     private String EmailFromUser;
    private String txId;

    private double amount;

    private String paymentMethod; // e.g., "PayPal"

    private LocalDateTime createdAt;
    private String Status;

    // Getters & Setters

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    @ManyToOne
    @JoinColumn(name = "user_email" ,referencedColumnName ="email" )
    private UserModel user;
}

