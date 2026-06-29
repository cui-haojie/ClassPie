package org.example.classpiserver.live;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.classpiserver.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
public class LiveWebSocketHandler extends TextWebSocketHandler {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private LiveEventHub liveEventHub;

    @Autowired
    private JwtService jwtService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attrs = session.getAttributes();
        String room = (String) attrs.get("room");
        if (room != null) {
            liveEventHub.join(room, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Map<String, Object> attrs = session.getAttributes();
        String room = (String) attrs.get("room");
        if (room != null) {
            liveEventHub.leave(room, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = JSON.readValue(message.getPayload(), Map.class);
        String action = String.valueOf(payload.getOrDefault("action", ""));
        if ("subscribe".equals(action)) {
            String token = payload.get("token") == null ? null : String.valueOf(payload.get("token"));
            String room = payload.get("room") == null ? null : String.valueOf(payload.get("room"));
            if (token == null || room == null || room.isBlank()) {
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            try {
                jwtService.parseAccount(token);
            } catch (Exception ex) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            Object oldRoom = session.getAttributes().get("room");
            if (oldRoom != null) {
                liveEventHub.leave(String.valueOf(oldRoom), session);
            }
            session.getAttributes().put("room", room);
            liveEventHub.join(room, session);
            session.sendMessage(new TextMessage("{\"type\":\"subscribed\",\"room\":\"" + room + "\"}"));
        }
    }
}
