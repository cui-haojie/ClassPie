package org.example.classpiserver.service;

import org.apache.ibatis.annotations.Param;
import org.example.classpiserver.dto.CourseRequest;
import org.example.classpiserver.entity.*;

import java.util.List;

public interface UserService {
    boolean addAccount(Accounts account);
    boolean changePassword(String password, String account);
    Accounts login(String account, String password);
    List<Long> getCourseIdByAccount(String account);
    boolean selectAccountByAccount(String account);
    Accounts getAccount(String account);
    Course getCourseByCode(String code, String account);
    List<Course> getCourseByCourseId(List<Long> id);
    void addCourse(String account,Long class_id);
    boolean togglePinCourse(Long courseId, Boolean isPinned);
    String getAccountName(String account);
    boolean addCourse(CourseRequest course);
    boolean addTeacherCourse(String account);
    Course getCourseById(Long id);
    Integer getCountByCourseId(Long id);
    boolean deleteCourse(Long id);
    boolean addHomework(Homework homework,Integer class_id);
    Integer getCountByClassId(Integer class_id);
    List<Homework> getHomeworkByClassId(Integer class_id);
    Homework getHomeworkById(Integer id);
    String getAccountStatus(String account);
    List<Content> getContentById(Long id);
    boolean setContentScore(int newScore,Long content_id,String account);
    boolean addContent(Content content);
}
