package org.example.classpiserver.service.activity;

import org.example.classpiserver.dto.activity.AddActivityReplyRequest;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseActivityReply;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.activity.ActivityMapper;
import org.example.classpiserver.service.notification.NotificationService;
import org.example.classpiserver.util.FileStorageService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private static final Set<String> COURSE_ACTIVITY_TYPES = Set.of(
            "interaction", "topic", "material", "test", "announcement"
    );

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<CourseActivity> getCourseActivities(Integer classId, String type, String account) {
        if (classId == null || type == null || !COURSE_ACTIVITY_TYPES.contains(type)) {
            return List.of();
        }
        List<CourseActivity> activities;
        if ("test".equals(type)) {
            boolean isTeacher = "老师".equals(accountMapper.getAccountStatus(account));
            activities = isTeacher
                    ? activityMapper.getCourseTestsByClassId(classId)
                    : activityMapper.getPublishedCourseTestsByClassId(classId);
        } else if ("interaction".equals(type)) {
            activities = activityMapper.getCourseInteractionsByClassId(classId);
        } else {
            activities = activityMapper.getCourseActivitiesByType(classId, type);
        }
        for (CourseActivity activity : activities) {
            normalizeActivityTimes(activity);
        }
        return activities;
    }

    @Override
    public Integer countCourseActivities(Integer classId, String type) {
        if (classId == null || type == null || !COURSE_ACTIVITY_TYPES.contains(type)) {
            return 0;
        }
        Integer count = activityMapper.countCourseActivitiesByType(classId, type);
        return count == null ? 0 : count;
    }

    @Override
    public boolean addCourseActivity(CourseActivity activity, Integer classId, MultipartFile attachment) {
        if (activity == null || classId == null || activity.getTitle() == null || activity.getTitle().isBlank()) {
            return false;
        }
        if (activity.getType() == null || !COURSE_ACTIVITY_TYPES.contains(activity.getType())) {
            return false;
        }
        if (activity.getCreator_account() == null || activity.getCreator_account().isBlank()) {
            return false;
        }
        if ("test".equals(activity.getType())) {
            return false;
        }
        if ("interaction".equals(activity.getType())) {
            return false;
        }
        activity.setClass_id(classId.longValue());
        activity.setPublish_status("published");
        if (activity.getDeadline() != null && !activity.getDeadline().isBlank()) {
            activity.setDeadline(HomeworkDeadlineUtil.normalizeInput(activity.getDeadline()));
        } else {
            activity.setDeadline(null);
            activity.setStart_time(null);
        }
        if (attachment != null && !attachment.isEmpty()) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.saveHomeworkAttachment(attachment);
                activity.setAttachment_url(stored.url());
                activity.setAttachment_name(stored.originalName());
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (IOException ex) {
                System.err.println("资料附件上传失败: " + ex.getMessage());
                return false;
            }
        }
        boolean ok = activityMapper.addCourseActivity(activity);
        if (ok) {
            Long activityId = activity.getId();
            if ("announcement".equals(activity.getType())) {
                notificationService.notifyAnnouncementPublished(classId, activityId, activity.getTitle());
            } else if ("topic".equals(activity.getType())) {
                notificationService.notifyTopicPublished(classId, activityId, activity.getTitle());
            } else if ("material".equals(activity.getType())) {
                notificationService.notifyMaterialPublished(classId, activityId, activity.getTitle());
            }
        }
        return ok;
    }

    @Override
    public CourseActivity getCourseActivityById(Long activityId) {
        if (activityId == null) {
            return null;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(activityId);
        normalizeActivityTimes(activity);
        return activity;
    }

    @Override
    public List<CourseActivityReply> getActivityReplies(Long activityId) {
        if (activityId == null) {
            return List.of();
        }
        List<CourseActivityReply> replies = activityMapper.getActivityReplies(activityId);
        for (CourseActivityReply reply : replies) {
            if (reply.getCreate_time() != null) {
                reply.setCreate_time(HomeworkDeadlineUtil.formatDisplay(reply.getCreate_time()));
            }
        }
        return replies;
    }

    @Override
    public boolean addActivityReply(AddActivityReplyRequest request, MultipartFile image) {
        if (request == null || request.getActivity_id() == null || request.getAccount() == null
                || request.getAccount().isBlank()) {
            return false;
        }
        String text = request.getContent() == null ? "" : request.getContent().trim();
        boolean hasImage = image != null && !image.isEmpty();
        if (text.isEmpty() && !hasImage) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(request.getActivity_id());
        if (activity == null || "test".equals(activity.getType()) || "interaction".equals(activity.getType())) {
            return false;
        }
        CourseActivityReply reply = new CourseActivityReply();
        reply.setActivity_id(request.getActivity_id());
        reply.setAccount(request.getAccount());
        reply.setContent(text);
        if (hasImage) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.saveTopicImage(image);
                reply.setAttachment_url(stored.url());
                reply.setAttachment_name(stored.originalName());
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (IOException ex) {
                System.err.println("话题图片上传失败: " + ex.getMessage());
                return false;
            }
        }
        return activityMapper.addActivityReply(reply);
    }

    @Override
    public boolean deleteActivity(Long activityId, String teacherAccount) {
        if (activityId == null || teacherAccount == null) {
            return false;
        }
        CourseActivity activity = activityMapper.getCourseActivityById(activityId);
        if (activity == null || "test".equals(activity.getType()) || "interaction".equals(activity.getType())) {
            return false;
        }
        if (!teacherAccount.equals(activity.getCreator_account())) {
            return false;
        }
        return activityMapper.deleteActivityById(activityId);
    }

    private void normalizeActivityTimes(CourseActivity activity) {
        if (activity == null) {
            return;
        }
        if (activity.getDeadline() != null) {
            activity.setDeadline(HomeworkDeadlineUtil.formatDisplay(activity.getDeadline()));
        }
        if (activity.getStart_time() != null) {
            activity.setStart_time(HomeworkDeadlineUtil.formatDisplay(activity.getStart_time()));
        }
        if (activity.getCreate_time() != null) {
            activity.setCreate_time(HomeworkDeadlineUtil.formatDisplay(activity.getCreate_time()));
        }
    }
}
