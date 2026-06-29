package org.example.classpiserver.service.course;

import org.example.classpiserver.dto.course.ArchiveCourseRequest;
import org.example.classpiserver.dto.course.CourseOrderRequest;
import org.example.classpiserver.dto.course.CourseRequest;
import org.example.classpiserver.dto.course.CourseUpdateRequest;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.entity.SchoolClass;

import java.util.List;

public interface CourseService {
    List<Long> getCourseIdByAccount(String account);
    List<Long> getArchivedCourseIdByAccount(String account);
    Course getCourseByCode(String account, String code);
    void addCourse(String account, Long classId);
    List<Course> getCourseByCourseId(List<Long> id);
    List<Course> getCourseByCourseIdWithPinStatus(String account, List<Long> id);
    boolean togglePinCourse(String account, Long courseId, Boolean isPinned);
    Course addCourse(CourseRequest course);
    boolean updateCourseOrder(CourseOrderRequest request);
    boolean addTeacherCourse(String account);
    Course getCourseById(Long id);
    Integer getCountByCourseId(Long id);
    boolean leaveCourse(String account, Long id);
    boolean updateCourseInfo(CourseUpdateRequest request);
    boolean archiveCourse(ArchiveCourseRequest request);
    List<CourseMember> getCourseMembers(Long classId);
}
