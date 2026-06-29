package org.example.classpiserver.service.notification;

import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.Notification;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.mapper.notification.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Notification> getNotifications(String account) {
        return notificationMapper.getNotifications(account);
    }

    @Override
    public Integer getUnreadNotificationCount(String account) {
        return notificationMapper.getUnreadNotificationCount(account);
    }

    @Override
    public boolean markNotificationRead(Integer id, String account) {
        if (id == null || account == null || account.isBlank()) {
            return false;
        }
        return notificationMapper.markNotificationRead(id, account);
    }

    @Override
    public boolean markAllNotificationsRead(String account) {
        if (account == null || account.isBlank()) {
            return false;
        }
        return notificationMapper.markAllNotificationsRead(account);
    }

    @Override
    public void notifyHomeworkPublished(Integer classId, Integer homeworkId, String homeworkName) {
        notifyStudents(classId, homeworkId, "homework", "新作业发布：" + homeworkName);
    }

    @Override
    public void notifyHomeworkRemind(String studentAccount, Integer classId, Integer homeworkId, String message) {
        Notification notification = new Notification();
        notification.setAccount(studentAccount);
        notification.setClass_id(classId);
        notification.setHomework_id(homeworkId);
        notification.setType("remind");
        notification.setMessage(message);
        notificationMapper.addNotification(notification);
    }

    @Override
    public void notifyAnnouncementPublished(Integer classId, Long activityId, String title) {
        notifyStudents(classId, toRefId(activityId), "announcement", "新公告：" + title);
    }

    @Override
    public void notifyTopicPublished(Integer classId, Long activityId, String title) {
        notifyStudents(classId, toRefId(activityId), "topic", "新话题：" + title);
    }

    @Override
    public void notifyMaterialPublished(Integer classId, Long activityId, String title) {
        notifyStudents(classId, toRefId(activityId), "material", "新资料：" + title);
    }

    @Override
    public void notifyTestPublished(Integer classId, Long activityId, String title) {
        notifyStudents(classId, toRefId(activityId), "test", "新测试发布：" + title);
    }

    @Override
    public void notifyInteractionPublished(Integer classId, Long activityId, String title) {
        notifyStudents(classId, toRefId(activityId), "interaction", "课堂互动开始：" + title);
    }

    @Override
    public void notifyAttendanceStarted(Integer classId, Long sessionId) {
        notifyStudents(classId, toRefId(sessionId), "attendance", "老师已发起签到，请尽快完成考勤");
    }

    @Override
    public void notifyStudentPicked(CourseActivity activity, String studentAccount) {
        Notification notification = new Notification();
        notification.setAccount(studentAccount);
        notification.setClass_id(activity.getClass_id().intValue());
        notification.setHomework_id(activity.getId().intValue());
        notification.setType("interaction_pick");
        notification.setMessage("老师点名请你回答：" + activity.getTitle());
        notificationMapper.addNotification(notification);
    }

    private void notifyStudents(Integer classId, Integer refId, String type, String message) {
        if (classId == null || type == null || message == null) {
            return;
        }
        List<CourseMember> members = courseMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setHomework_id(refId);
            notification.setType(type);
            notification.setMessage(message);
            notificationMapper.addNotification(notification);
        }
    }

    private Integer toRefId(Long id) {
        return id == null ? null : id.intValue();
    }
}
