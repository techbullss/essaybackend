package essay.essay.Configarions;



import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class PresenceTracker {
    private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    public void addUser(String email, String sessionId) {
        onlineUsers.put(email, sessionId);
    }

    public void removeUser(String email) {
        onlineUsers.remove(email);
    }

    public boolean isOnline(String email) {
        return onlineUsers.containsKey(email);
    }

}

