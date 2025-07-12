package essay.essay.Configarions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    @Autowired
    private PresenceTracker presenceTracker;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = sha.getUser(); // now set by CustomHandshakeHandler
        if (user != null) {
            String email = user.getName();
            String sessionId = sha.getSessionId();
            presenceTracker.addUser(email, sessionId);
            System.out.println("🔵 CONNECTED: " + email);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();

        // Reverse lookup by session ID
        presenceTracker.getOnlineUsers().entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
        System.out.println("🔴 DISCONNECTED: Session " + sessionId);
    }
}
