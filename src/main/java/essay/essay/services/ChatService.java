package essay.essay.services;

import essay.essay.Models.Message;
import essay.essay.Models.UserModel;
import essay.essay.repository.MessegeRepo;
import essay.essay.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final MessegeRepo messageRepository;
    private final UserRepo userRepo;

    @Autowired
    public ChatService(MessegeRepo messageRepository, UserRepo userRepo) {
        this.messageRepository = messageRepository;
        this.userRepo = userRepo;
    }

    public Message saveMessage(Message message) {
        // Verify users exist
        UserModel sender = userRepo.findByEmail(message.getSenderEmail())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        UserModel recipient = userRepo.findByEmail(message.getRecipientEmail())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getConversation(String senderEmail, String recipientEmail) {
        return messageRepository.findConversation(senderEmail, recipientEmail);
    }

    public List<UserModel> getInbox(String email) {
        return messageRepository.findInboxUsers(email);
    }
    public void markMessagesAsRead(String senderEmail, String recipientEmail) {
        List<Message> unreadMessages = messageRepository.findBySenderEmailAndRecipientEmailAndIsReadFalse(senderEmail, recipientEmail);
        for (Message msg : unreadMessages) {
            msg.setRead(true);
        }
        messageRepository.saveAll(unreadMessages);
    }
    public Message saveAndSendMessage(Message message) {
        message.setDelivered(true); // optimistic delivery
        return messageRepository.save(message);
    }
}