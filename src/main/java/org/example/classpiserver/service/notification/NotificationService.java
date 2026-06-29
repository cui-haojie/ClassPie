package org.example.classpiserver.service.notification;

import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotifications(String account);
    Integer getUnreadNotificationCount(String account);
    boolean markNotificationRead(Integer id, String account);
    boolean markAllNotificationsRead(String account);
    void notifyHomeworkPublished(Integer classId, Integer homeworkId, String homeworkName);
    void notifyHomeworkRemind(String studentAccount, Integer classId, Integer homeworkId, String message);
    void notifyAnnouncementPublished(Integer classId, String title);
    void notifyTestPublished(Integer classId, String title);
    void notifyInteractionPublished(Integer classId, Long activityId, String title);
    void notifyStudentPicked(CourseActivity activity, String studentAccount);
}
