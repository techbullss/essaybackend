package essay.essay.Models;

import essay.essay.repository.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tempId;
    private String senderEmail;
    private String recipientEmail;
    private String content;
    private LocalDateTime timestamp;
    private String FileUrl;
    private String FileName;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    @Column(name = "is_read")
    private boolean isRead = false;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Column(name = "is_delivered",nullable = true)
    private boolean isDelivered = false;

    @Column(name = "is_failed",nullable = true)
    private boolean isFailed = false;

    // Getters and setters

    public boolean isDelivered() { return isDelivered; }
    public void setDelivered(boolean delivered) { isDelivered = delivered; }

    public boolean isFailed() { return isFailed; }
    public void setFailed(boolean failed) { isFailed = failed; }

    public Boolean getRead() {
        return isRead;
    }
}
