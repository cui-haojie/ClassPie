package org.example.classpiserver.service.activity;

import org.example.classpiserver.dto.activity.AddActivityReplyRequest;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseActivityReply;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ActivityService {
    List<CourseActivity> getCourseActivities(Integer classId, String type, String account);
    Integer countCourseActivities(Integer classId, String type);
    boolean addCourseActivity(CourseActivity activity, Integer classId, MultipartFile attachment);
    CourseActivity getCourseActivityById(Long activityId);
    List<CourseActivityReply> getActivityReplies(Long activityId);
    boolean addActivityReply(AddActivityReplyRequest request, MultipartFile image);
    boolean deleteActivity(Long activityId, String teacherAccount);
}
