package org.example.classpiserver.controller.activity;

import org.example.classpiserver.dto.activity.ActivityIdRequest;
import org.example.classpiserver.dto.activity.AddActivityReplyRequest;
import org.example.classpiserver.dto.activity.CourseActivitiesRequest;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseActivityReply;
import org.example.classpiserver.service.activity.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/getCourseActivities")
    public List<CourseActivity> getCourseActivities(@RequestBody CourseActivitiesRequest request) {
        return activityService.getCourseActivities(request.getClass_id(), request.getType(), request.getAccount());
    }

    @PostMapping("/getCourseActivityCount")
    public Integer getCourseActivityCount(@RequestBody CourseActivitiesRequest request) {
        return activityService.countCourseActivities(request.getClass_id(), request.getType());
    }

    @PostMapping(value = "/addCourseActivity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addCourseActivity(
            @RequestParam("class_id") Integer classId,
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("creator_account") String creatorAccount,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "start_time", required = false) String startTime,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "attachment_url", required = false) String attachmentUrl,
            @RequestParam(value = "attachment_name", required = false) String attachmentName,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        CourseActivity activity = new CourseActivity();
        activity.setType(type);
        activity.setTitle(title);
        activity.setCreator_account(creatorAccount);
        activity.setContent(content);
        activity.setStart_time(startTime);
        activity.setDeadline(deadline);
        if (file == null || file.isEmpty()) {
            activity.setAttachment_url(attachmentUrl);
            activity.setAttachment_name(attachmentName);
        }
        return activityService.addCourseActivity(activity, classId, file);
    }

    @PostMapping("/getCourseActivityById")
    public CourseActivity getCourseActivityById(@RequestBody ActivityIdRequest request) {
        return activityService.getCourseActivityById(request.getActivity_id());
    }

    @PostMapping("/getActivityReplies")
    public List<CourseActivityReply> getActivityReplies(@RequestBody ActivityIdRequest request) {
        return activityService.getActivityReplies(request.getActivity_id());
    }

    @PostMapping(value = "/addActivityReply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addActivityReply(
            @RequestParam("activity_id") Long activityId,
            @RequestParam("account") String account,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        AddActivityReplyRequest request = new AddActivityReplyRequest();
        request.setActivity_id(activityId);
        request.setAccount(account);
        request.setContent(content);
        return activityService.addActivityReply(request, file);
    }
}
