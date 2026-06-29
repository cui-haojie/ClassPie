package org.example.classpiserver.live;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class LiveEventHub {

    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public void join(String roomKey, WebSocketSession session) {
        rooms.computeIfAbsent(roomKey, k -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void leave(String roomKey, WebSocketSession session) {
        Set<WebSocketSession> sessions = rooms.get(roomKey);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                rooms.remove(roomKey);
            }
        }
    }

    public void broadcast(String roomKey, String jsonPayload) {
        Set<WebSocketSession> sessions = rooms.get(roomKey);
        if (sessions == null) {
            return;
        }
        TextMessage message = new TextMessage(jsonPayload);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static String courseRoom(Long classId) {
        return "course:" + classId;
    }

    public static String interactionRoom(Long activityId) {
        return "interaction:" + activityId;
    }

    public static String attendanceRoom(Long sessionId) {
        return "attendance:" + sessionId;
    }
}
