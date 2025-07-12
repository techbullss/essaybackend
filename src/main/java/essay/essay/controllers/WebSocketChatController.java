package essay.essay.controllers;
import essay.essay.Configarions.PresenceTracker;
import essay.essay.Models.Message;
import essay.essay.repository.MessageStatus;
import essay.essay.repository.MessegeRepo;
import essay.essay.services.ChatService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

@Controller
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
@Autowired
    MessegeRepo msg;
@Autowired
    PresenceTracker presenceTracker;
    @Autowired
    public WebSocketChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("/send")
    public void handleMessage(@Payload Message message, Principal principal) {
        String senderEmail = principal.getName();
        try {
            Message saved = chatService.saveMessage(message);
            boolean senderOnline = presenceTracker.isOnline(senderEmail);
            boolean recipientOnline = presenceTracker.isOnline(saved.getRecipientEmail());

            if (senderOnline && recipientOnline) {
                saved.setDelivered(true);
                saved.setStatus(MessageStatus.DELIVERED);
                chatService.saveMessage(saved);
                messagingTemplate.convertAndSendToUser(saved.getSenderEmail(), "/queue/deliveries", Map.of("messageId", saved.getId()));
                messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", saved);

                messagingTemplate.convertAndSendToUser(saved.getRecipientEmail(), "/queue/messages", saved);

            } else {
                saved.setDelivered(false);
                saved.setStatus(MessageStatus.SENT);
                chatService.saveMessage(saved);
                messagingTemplate.convertAndSendToUser(saved.getSenderEmail(), "/queue/deliveries", Map.of("messageId", saved.getId()));
                messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", saved);

                messagingTemplate.convertAndSendToUser(saved.getRecipientEmail(), "/queue/messages", saved);

            }



        } catch (Exception e) {
            System.err.println("❌ Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @MessageMapping("/messages/read")
    public void markMessageAsRead(@Payload Map<String, Object> payload, Principal principal) {
        String recipient = principal.getName();
System.out.println(recipient);
        if (payload == null || !payload.containsKey("messageId")) return;

        Long messageId = Long.valueOf(payload.get("messageId").toString());

        msg.findById(messageId).ifPresent(message -> {
            // Only mark as read if this user is the actual recipient and it's unread
            if (message.getRecipientEmail().equals(recipient) && !Boolean.TRUE.equals(message.getRead())) {
                message.setRead(true);
                message.setStatus(MessageStatus.READ);
                msg.save(message);

                // Notify the sender in real time
                messagingTemplate.convertAndSendToUser(
                        message.getSenderEmail(),
                        "/queue/reads",
                        Map.of("messageId", messageId)
                );
            }
        });
    }

    }






    // Handle typing indicators



