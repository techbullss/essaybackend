package essay.essay.Models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Order  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, updatable = false)
    private String orderId;
    private String email;
    private String topic;
    private String instructions;
    private String level;
    private String assignmentType;
    private String style;
    private int pages;
    private int slides;
    private String spacing;
    private int wordCount;
    private double price;
    private String deadline;
   private String orderStatus;
    private String paymentStatus;
    private Double Amount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    @ElementCollection
    @CollectionTable(name = "order_files",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "orderId"))
    @Column(name = "file_url")
    private List<String> fileUrls = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "order_files",
            joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "filename")
    private List<String> filenames = new ArrayList<>();
    // Automatically generate a UUID orderId when creating the order
    @PrePersist
    public void generateOrderId() {
        this.orderId = "ORD-" + UUID.randomUUID();
    }

}
