package org.example.classpiserver.service;

import org.example.classpiserver.dto.*;
import org.example.classpiserver.entity.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    boolean addAccount(Accounts account);
    boolean register(RegisterRequest request);
    boolean changePassword(String password, String account);
    Accounts login(String account, String password);
    List<Long> getCourseIdByAccount(String account);
    List<Long> getArchivedCourseIdByAccount(String account);
    boolean selectAccountByAccount(String account);
    Accounts getAccount(String account);
    Course getCourseByCode(String code, String account);
    List<Course> getCourseByCourseId(List<Long> id);
    void addCourse(String account,Long class_id);
    boolean togglePinCourse(Long courseId, Boolean isPinned);
    String getAccountName(String account);
    Course addCourse(CourseRequest course);
    List<SchoolClass> listSchoolClasses();
    SchoolClass createSchoolClass(SchoolClassRequest request);
    boolean joinStudentClass(JoinStudentClassRequest request);
    boolean updateStudentSchoolClasses(UpdateStudentSchoolClassRequest request);
    boolean updateCourseOrder(CourseOrderRequest request);
    List<SchoolClass> getStudentSchoolClasses(String account);
    ImportStudentResult importSchoolClassStudents(MultipartFile file, Integer schoolClassId);
    byte[] buildStudentImportTemplate();
    boolean addTeacherCourse(String account);
    Course getCourseById(Long id);
    Integer getCountByCourseId(Long id);
    boolean leaveCourse(String account, Long id);
    boolean updateCourseInfo(CourseUpdateRequest request);
    boolean archiveCourse(ArchiveCourseRequest request);
    List<CourseMember> getCourseMembers(Long classId);
    boolean addHomework(Homework homework,Integer class_id);
    Integer getCountByClassId(Integer class_id);
    List<Homework> getHomeworkByClassId(Integer class_id);
    Homework getHomeworkById(Integer id);
    String getAccountStatus(String account);
    List<Content> getContentById(Long id);
    boolean setContentScore(int newScore,Long content_id,String account);
    boolean addContent(Content content);
    boolean submitHomework(Long contentId, String account, String details, MultipartFile file);
    String uploadAvatar(String account, MultipartFile file);
    boolean updateAccount(Accounts account);
    List<Notification> getNotifications(String account);
    Integer getUnreadNotificationCount(String account);
    boolean markNotificationRead(Integer id, String account);
    boolean remindHomework(RemindHomeworkRequest request);
    List<CourseActivity> getCourseActivities(Integer classId, String type);
    Integer countCourseActivities(Integer classId, String type);
    boolean addCourseActivity(CourseActivity activity, Integer classId);
}
