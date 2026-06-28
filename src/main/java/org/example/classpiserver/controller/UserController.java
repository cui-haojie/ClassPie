package org.example.classpiserver.controller;

import org.example.classpiserver.dto.*;
import org.example.classpiserver.entity.Accounts;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.Courses_homework;
import org.example.classpiserver.entity.Homework;
import org.example.classpiserver.entity.Content;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseActivityReply;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.Notification;
import org.example.classpiserver.entity.SchoolClass;
import org.example.classpiserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/editor")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/courses")
    public List<Course> getCourses(@RequestBody AccountRequest accountRequest) {
        return userService.getCourseByCourseIdWithPinStatus(accountRequest.getAccount(), userService.getCourseIdByAccount(accountRequest.getAccount()));
    }

    @PostMapping("/archivedCourses")
    public List<Course> getArchivedCourses(@RequestBody AccountRequest accountRequest) {
        return userService.getCourseByCourseId(userService.getArchivedCourseIdByAccount(accountRequest.getAccount()));
    }

    @PostMapping("/account")
    public ResponseEntity<Accounts> getAccount(@RequestBody AccountRequest accountRequest) {
        Accounts result = userService.getAccount(accountRequest.getAccount());
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Accounts());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public boolean addAccount(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PutMapping("/change")
    public boolean changePassword(@RequestBody LoginRequest request) {
        return userService.changePassword(request.getPassword(), request.getAccount());
    }

    @PostMapping("/login")
    public Accounts login(@RequestBody LoginRequest request) {
        return userService.login(request.getAccount(), request.getPassword());
    }

    @PostMapping("/check")
    public boolean selectAccountByAccount(@RequestBody String account){
        return userService.selectAccountByAccount(account);
    }

    @PostMapping("/addCourse")
    public Course getCourseByCode(@RequestBody Account_CourseRequest request) {
        return userService.getCourseByCode(request.getAccount(),request.getCode());
    }

    @PostMapping("/selectTeacherName")
    public String getAccountName(@RequestBody AccountRequest accountRequest) {
        return userService.getAccountName(accountRequest.getAccount());
    }

    @PostMapping("/updatePinStatus")
    public boolean updatePinStatus(@RequestBody CourseId_IsPinnedRequest request) {
        return userService.togglePinCourse(request.getAccount(), request.getId(), request.isIs_pinned());
    }

    @PostMapping("/createCourse")
    public Course createCourse(@RequestBody CourseRequest request) {
        return userService.addCourse(request);
    }

    @PostMapping("/addTeacherCourse")
    public boolean addTeacherCourse (@RequestBody AccountRequest request){
        return userService.addTeacherCourse(request.getAccount());
    }

    @PostMapping("/getCourseById")
    public Course getCourseById(@RequestBody CourseIdRequest request) {
        return userService.getCourseById(request.getId());
    }

    @PostMapping("/getCountById")
    public Integer getCountById(@RequestBody CourseIdRequest request) {
        return userService.getCountByCourseId(request.getId());
    }

    @PostMapping("/deleteCourse")
    public boolean leaveCourse(@RequestBody LeaveCourseRequest request) {
        return userService.leaveCourse(request.getAccount(), request.getId());
    }

    @PutMapping("/updateCourse")
    public boolean updateCourse(@RequestBody CourseUpdateRequest request) {
        return userService.updateCourseInfo(request);
    }

    @PutMapping("/archiveCourse")
    public boolean archiveCourse(@RequestBody ArchiveCourseRequest request) {
        return userService.archiveCourse(request);
    }

    @PostMapping("/getCourseMembers")
    public List<CourseMember> getCourseMembers(@RequestBody CourseIdRequest request) {
        return userService.getCourseMembers(request.getId());
    }

    @PostMapping(value = "/addHomework", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addHomework(
            @RequestParam("class_id") Integer class_id,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("deadline") String deadline,
            @RequestParam(value = "details", required = false) String details,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        Homework homework = new Homework();
        homework.setName(name);
        homework.setType(type);
        homework.setDeadline(deadline);
        homework.setDetails(details);
        return userService.addHomework(homework, class_id, file);
    }

    @PostMapping("/getCountByClassId")
    public Integer getCountByClassId(@RequestBody ClassIdRequest request) {
        return userService.getCountByClassId(request.getClass_id());
    }

    @PostMapping("/getHomeworkByClassId")
    public List<Homework> getHomeworkByClassId(@RequestBody ClassIdRequest request) {
        return userService.getHomeworkByClassId(request.getClass_id());
    }

    @PostMapping("/getHomeworkById")
    public Homework getHomeworkById(@RequestBody HomeworkIdRequest request) {
        return userService.getHomeworkById(request.getHomework_id());
    }

    @PostMapping("/getAccountStatus")
    public String getAccountStatus(@RequestBody AccountRequest request) {
        return userService.getAccountStatus(request.getAccount());
    }

    @PostMapping("/getContentById")
    public List<Content> getContentById(@RequestBody ContentIdRequest request) {
        return userService.getContentById(request.getContentId());
    }

    @PutMapping("/setScore")
    public boolean setScore(@RequestBody ScoreRequest request) {
        return userService.setContentScore(request.getScore(),request.getContent_id(),request.getAccount());
    }

    @PostMapping("/addContent")
    public boolean addContent(@RequestBody Content content) {
        return userService.addContent(content);
    }

    @PostMapping(value = "/submitHomework", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean submitHomework(
            @RequestParam("content_id") Long contentId,
            @RequestParam("account") String account,
            @RequestParam(value = "details", required = false) String details,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return userService.submitHomework(contentId, account, details, file);
    }

    @PostMapping(value = "/uploadAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<java.util.Map<String, String>> uploadAvatar(
            @RequestParam("account") String account,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(account, file);
        if (avatarUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("message", "头像上传失败"));
        }
        return ResponseEntity.ok(java.util.Map.of("avatar_url", avatarUrl));
    }

    @PutMapping("/updateAccount")
    public boolean updateAccount(@RequestBody Accounts account) {
        return userService.updateAccount(account);
    }

    @PostMapping("/notifications")
    public List<Notification> getNotifications(@RequestBody AccountRequest request) {
        return userService.getNotifications(request.getAccount());
    }

    @PostMapping("/notificationCount")
    public Integer getNotificationCount(@RequestBody AccountRequest request) {
        return userService.getUnreadNotificationCount(request.getAccount());
    }

    @PutMapping("/readNotification")
    public boolean readNotification(@RequestBody Notification request) {
        return userService.markNotificationRead(request.getId(), request.getAccount());
    }

    @PutMapping("/readAllNotifications")
    public boolean readAllNotifications(@RequestBody AccountRequest request) {
        return userService.markAllNotificationsRead(request.getAccount());
    }

    @PostMapping("/remindHomework")
    public boolean remindHomework(@RequestBody RemindHomeworkRequest request) {
        return userService.remindHomework(request);
    }

    @PostMapping("/listSchoolClasses")
    public List<SchoolClass> listSchoolClasses() {
        return userService.listSchoolClasses();
    }

    @PostMapping("/createSchoolClass")
    public SchoolClass createSchoolClass(@RequestBody SchoolClassRequest request) {
        return userService.createSchoolClass(request);
    }

    @PostMapping("/joinStudentClass")
    public boolean joinStudentClass(@RequestBody JoinStudentClassRequest request) {
        return userService.joinStudentClass(request);
    }

    @PutMapping("/updateStudentSchoolClasses")
    public boolean updateStudentSchoolClasses(@RequestBody UpdateStudentSchoolClassRequest request) {
        return userService.updateStudentSchoolClasses(request);
    }

    @PutMapping("/updateCourseOrder")
    public boolean updateCourseOrder(@RequestBody CourseOrderRequest request) {
        return userService.updateCourseOrder(request);
    }

    @PostMapping("/studentSchoolClass")
    public List<SchoolClass> getStudentSchoolClass(@RequestBody AccountRequest request) {
        return userService.getStudentSchoolClasses(request.getAccount());
    }

    @PostMapping(value = "/importSchoolClassStudents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportStudentResult importSchoolClassStudents(
            @RequestParam("file") MultipartFile file,
            @RequestParam("school_class_id") Integer schoolClassId) {
        return userService.importSchoolClassStudents(file, schoolClassId);
    }

    @PostMapping("/getCourseActivities")
    public List<CourseActivity> getCourseActivities(@RequestBody CourseActivitiesRequest request) {
        return userService.getCourseActivities(request.getClass_id(), request.getType(), request.getAccount());
    }

    @PostMapping("/getCourseActivityCount")
    public Integer getCourseActivityCount(@RequestBody CourseActivitiesRequest request) {
        return userService.countCourseActivities(request.getClass_id(), request.getType());
    }

    @PostMapping(value = "/addCourseActivity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addCourseActivity(
            @RequestParam("class_id") Integer class_id,
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("creator_account") String creator_account,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "start_time", required = false) String start_time,
            @RequestParam(value = "deadline", required = false) String deadline,
            @RequestParam(value = "attachment_url", required = false) String attachment_url,
            @RequestParam(value = "attachment_name", required = false) String attachment_name,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        CourseActivity activity = new CourseActivity();
        activity.setType(type);
        activity.setTitle(title);
        activity.setCreator_account(creator_account);
        activity.setContent(content);
        activity.setStart_time(start_time);
        activity.setDeadline(deadline);
        if (file == null || file.isEmpty()) {
            activity.setAttachment_url(attachment_url);
            activity.setAttachment_name(attachment_name);
        }
        return userService.addCourseActivity(activity, class_id, file);
    }

    @PostMapping("/getCourseActivityById")
    public CourseActivity getCourseActivityById(@RequestBody ActivityIdRequest request) {
        return userService.getCourseActivityById(request.getActivity_id());
    }

    @PostMapping("/getActivityReplies")
    public List<CourseActivityReply> getActivityReplies(@RequestBody ActivityIdRequest request) {
        return userService.getActivityReplies(request.getActivity_id());
    }

    @PostMapping(value = "/addActivityReply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean addActivityReply(
            @RequestParam("activity_id") Long activity_id,
            @RequestParam("account") String account,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        AddActivityReplyRequest request = new AddActivityReplyRequest();
        request.setActivity_id(activity_id);
        request.setAccount(account);
        request.setContent(content);
        return userService.addActivityReply(request, file);
    }

    @PostMapping("/addCourseTest")
    public boolean addCourseTest(@RequestBody AddCourseTestRequest request) {
        try {
            return userService.addCourseTest(request);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @PostMapping("/saveCourseTestDraft")
    public SaveTestDraftResult saveCourseTestDraft(@RequestBody AddCourseTestRequest request) {
        return userService.saveCourseTestDraft(request);
    }

    @PostMapping("/getTestDetail")
    public TestDetailDTO getTestDetail(@RequestBody GetTestDetailRequest request) {
        return userService.getTestDetail(request.getActivity_id(), request.getAccount());
    }

    @PostMapping("/submitTest")
    public boolean submitTest(@RequestBody SubmitTestRequest request) {
        return userService.submitTest(request);
    }

    @GetMapping("/downloadStudentImportTemplate")
    public ResponseEntity<byte[]> downloadStudentImportTemplate() {
        byte[] bytes = userService.buildStudentImportTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
