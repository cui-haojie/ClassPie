package org.example.classpiserver.service.notification;

import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotifications(String account);
    Integer getUnreadNotificationCount(String account);
    boolean markNotificationRead(Integer id, String account);
    boolean markAllNotificationsRead(String account);
    void notifyHomeworkPublished(Integer classId, Integer homeworkId, String homeworkName);
    void notifyHomeworkRemind(String studentAccount, Integer classId, Integer homeworkId, String message);
    void notifyAnnouncementPublished(Integer classId, Long activityId, String title);
    void notifyTopicPublished(Integer classId, Long activityId, String title);
    void notifyMaterialPublished(Integer classId, Long activityId, String title);
    void notifyTestPublished(Integer classId, Long activityId, String title);
    void notifyInteractionPublished(Integer classId, Long activityId, String title);
    void notifyAttendanceStarted(Integer classId, Long sessionId);
    void notifyStudentPicked(CourseActivity activity, String studentAccount);
}
