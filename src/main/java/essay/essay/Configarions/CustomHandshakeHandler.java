package essay.essay.Configarions;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String email = "anonymous"; // fallback value
        String query = request.getURI().getQuery();

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("email=")) {
                    email = param.substring("email=".length());
                    break;
                }
            }
        }

        String finalEmail = email;
        return () -> finalEmail; // returns a Principal with getName() = email
    }
}
