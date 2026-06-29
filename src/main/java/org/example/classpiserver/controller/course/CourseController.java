package org.example.classpiserver.controller.course;

import org.example.classpiserver.dto.account.AccountRequest;
import org.example.classpiserver.dto.account.Account_CourseRequest;
import org.example.classpiserver.dto.course.ArchiveCourseRequest;
import org.example.classpiserver.dto.course.CourseIdRequest;
import org.example.classpiserver.dto.course.CourseId_IsPinnedRequest;
import org.example.classpiserver.dto.course.CourseOrderRequest;
import org.example.classpiserver.dto.course.CourseRequest;
import org.example.classpiserver.dto.course.CourseUpdateRequest;
import org.example.classpiserver.dto.course.LeaveCourseRequest;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/courses")
    public List<Course> getCourses(@RequestBody AccountRequest request) {
        return courseService.getCourseByCourseIdWithPinStatus(
                request.getAccount(), courseService.getCourseIdByAccount(request.getAccount()));
    }

    @PostMapping("/archivedCourses")
    public List<Course> getArchivedCourses(@RequestBody AccountRequest request) {
        return courseService.getCourseByCourseId(courseService.getArchivedCourseIdByAccount(request.getAccount()));
    }

    @PostMapping("/addCourse")
    public Course getCourseByCode(@RequestBody Account_CourseRequest request) {
        return courseService.getCourseByCode(request.getAccount(), request.getCode());
    }

    @PostMapping("/updatePinStatus")
    public boolean updatePinStatus(@RequestBody CourseId_IsPinnedRequest request) {
        return courseService.togglePinCourse(request.getAccount(), request.getId(), request.isIs_pinned());
    }

    @PostMapping("/createCourse")
    public Course createCourse(@RequestBody CourseRequest request) {
        return courseService.addCourse(request);
    }

    @PostMapping("/addTeacherCourse")
    public boolean addTeacherCourse(@RequestBody AccountRequest request) {
        return courseService.addTeacherCourse(request.getAccount());
    }

    @PostMapping("/getCourseById")
    public Course getCourseById(@RequestBody CourseIdRequest request) {
        return courseService.getCourseById(request.getId());
    }

    @PostMapping("/getCountById")
    public Integer getCountById(@RequestBody CourseIdRequest request) {
        return courseService.getCountByCourseId(request.getId());
    }

    @PostMapping("/deleteCourse")
    public boolean leaveCourse(@RequestBody LeaveCourseRequest request) {
        return courseService.leaveCourse(request.getAccount(), request.getId());
    }

    @PutMapping("/updateCourse")
    public boolean updateCourse(@RequestBody CourseUpdateRequest request) {
        return courseService.updateCourseInfo(request);
    }

    @PutMapping("/archiveCourse")
    public boolean archiveCourse(@RequestBody ArchiveCourseRequest request) {
        return courseService.archiveCourse(request);
    }

    @PostMapping("/getCourseMembers")
    public List<CourseMember> getCourseMembers(@RequestBody CourseIdRequest request) {
        return courseService.getCourseMembers(request.getId());
    }

    @PutMapping("/updateCourseOrder")
    public boolean updateCourseOrder(@RequestBody CourseOrderRequest request) {
        return courseService.updateCourseOrder(request);
    }
}
