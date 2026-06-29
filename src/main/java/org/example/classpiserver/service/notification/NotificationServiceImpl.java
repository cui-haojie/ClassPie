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
        List<CourseMember> members = courseMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setHomework_id(homeworkId);
            notification.setType("homework");
            notification.setMessage("新作业发布：" + homeworkName);
            notificationMapper.addNotification(notification);
        }
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
    public void notifyAnnouncementPublished(Integer classId, String title) {
        List<CourseMember> members = courseMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setType("announcement");
            notification.setMessage("新公告：" + title);
            notificationMapper.addNotification(notification);
        }
    }

    @Override
    public void notifyTestPublished(Integer classId, String title) {
        List<CourseMember> members = courseMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setType("test");
            notification.setMessage("新测试发布：" + title);
            notificationMapper.addNotification(notification);
        }
    }

    @Override
    public void notifyInteractionPublished(Integer classId, Long activityId, String title) {
        List<CourseMember> members = courseMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setHomework_id(activityId == null ? null : activityId.intValue());
            notification.setType("interaction");
            notification.setMessage("课堂互动开始：" + title);
            notificationMapper.addNotification(notification);
        }
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
}
