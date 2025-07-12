package essay.essay.controllers;

import essay.essay.Models.Message;
import essay.essay.Models.UserModel;
import essay.essay.repository.MessegeRepo;
import essay.essay.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:3000/")
@RequestMapping("/api/messages")
public class MessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
 @Autowired
 MessegeRepo messegeRepo;
    @Autowired
    public MessageController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/msg")
    public List<Message> getMessages(
            @RequestParam String senderEmail,
            @RequestParam String recipientEmail) {
        return chatService.getConversation(senderEmail, recipientEmail);
    }

    @GetMapping("/inbox")
    public List<UserModel> getInbox(@RequestParam String email) {
        return chatService.getInbox(email);
    }

    @PostMapping("/sendmsg")
    public Message sendMessage(@RequestBody Message message) {
        Message savedMessage = chatService.saveAndSendMessage(message);

        try {
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientEmail(),
                    "/queue/messages",
                    savedMessage
            );
        } catch (Exception e) {
            savedMessage.setDelivered(false);
            savedMessage.setFailed(true);
            messegeRepo.save(savedMessage);
        }

        return savedMessage;
    }

    @PostMapping(value="/sendfile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendMessageWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderEmail") String senderEmail,
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("content") String content) {

        try {
            // Save file to disk (or cloud)
            String uploadDir = new File("C:\\Users\\bwana\\Downloads\\essay\\uploads").getAbsolutePath(); // inside project folder
            Files.createDirectories(Paths.get(uploadDir));

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);

            Files.write(filePath, file.getBytes());

            // Save message
            Message msg = new Message();
            msg.setSenderEmail(senderEmail);
            msg.setRecipientEmail(recipientEmail);
            msg.setContent(content);
            msg.setFileName(filename);
            msg.setFileUrl("http://localhost:8080/uploads/"+filename); // Add this field to your Message model if it doesn't exist
            msg.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

            Message savedMessage = chatService.saveMessage(msg);

            // Send through WebSocket
            messagingTemplate.convertAndSendToUser(
                    recipientEmail,
                    "/queue/messages",
                    savedMessage
            );

            return ResponseEntity.ok(savedMessage);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }
    @PostMapping("/isread")
    public ResponseEntity<String> markMessagesAsRead(@RequestBody Map<String, String> body) {
        String senderEmail = body.get("senderEmail");
        String recipientEmail = body.get("recipientEmail");

        if (senderEmail == null || recipientEmail == null) {
            return ResponseEntity.badRequest().body("Missing sender or recipient email.");
        }

        chatService.markMessagesAsRead(senderEmail, recipientEmail);
        return ResponseEntity.ok("Messages marked as read.");
    }
    @PostMapping("/mark-delivered")
    public ResponseEntity<String> markDelivered(@RequestBody Map<String, Object> body) {
        Long messageId = Long.valueOf(body.get("messageId").toString());
        Optional<Message> msgOpt = messegeRepo.findById(messageId);

        if (msgOpt.isPresent()) {
            Message msg = msgOpt.get();
            msg.setDelivered(true);
            messegeRepo.save(msg);
            return ResponseEntity.ok("Message marked as delivered");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
    }
}
