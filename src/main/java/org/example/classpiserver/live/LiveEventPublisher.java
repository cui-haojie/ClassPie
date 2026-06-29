package org.example.classpiserver.live;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LiveEventPublisher {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private LiveEventHub liveEventHub;

    public void publishCourse(Long classId, String eventType, Object data) {
        publish(LiveEventHub.courseRoom(classId), eventType, data);
    }

    public void publishInteraction(Long activityId, String eventType, Object data) {
        publish(LiveEventHub.interactionRoom(activityId), eventType, data);
    }

    public void publishAttendance(Long sessionId, String eventType, Object data) {
        publish(LiveEventHub.attendanceRoom(sessionId), eventType, data);
    }

    private void publish(String room, String eventType, Object data) {
        try {
            String json = JSON.writeValueAsString(Map.of("type", eventType, "room", room, "data", data));
            liveEventHub.broadcast(room, json);
        } catch (Exception ignored) {
        }
    }
}
