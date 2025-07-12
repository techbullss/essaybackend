package essay.essay.repository;

import essay.essay.Models.Message;
import essay.essay.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessegeRepo extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderEmail = :user1 AND m.recipientEmail = :user2) OR " +
            "(m.senderEmail = :user2 AND m.recipientEmail = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversation(String user1, String user2);

    @Query("SELECT DISTINCT u FROM UserModel u WHERE u.email IN (" +
            "SELECT DISTINCT m.senderEmail FROM Message m WHERE m.recipientEmail = :email " +
            "UNION " +
            "SELECT DISTINCT m.recipientEmail FROM Message m WHERE m.senderEmail = :email)")
    List<UserModel> findInboxUsers(String email);
    List<Message> findBySenderEmailAndRecipientEmailAndIsReadFalse(String senderEmail, String recipientEmail);

}
