package org.example.classpiserver.controller;

import org.example.classpiserver.dto.*;
import org.example.classpiserver.entity.Accounts;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.Courses_homework;
import org.example.classpiserver.entity.Homework;
import org.example.classpiserver.entity.Content;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.Notification;
import org.example.classpiserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/editor")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/courses")
    public List<Course> getCourses(@RequestBody AccountRequest accountRequest) {
        return userService.getCourseByCourseId(userService.getCourseIdByAccount(accountRequest.getAccount()));
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
    public boolean addAccount(@RequestBody Accounts account) {
        return userService.addAccount(account);
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
        return userService.togglePinCourse(request.getId(),request.isIs_pinned());
    }

    @PostMapping("/createCourse")
    public boolean createCourse(@RequestBody CourseRequest request) {
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

    @PostMapping("/addHomework")
    public boolean addHomework(@RequestBody HomeworkRequest request) {
        return userService.addHomework(request.getHomework(),request.getClass_id());
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

    @PostMapping("/remindHomework")
    public boolean remindHomework(@RequestBody RemindHomeworkRequest request) {
        return userService.remindHomework(request);
    }
}
