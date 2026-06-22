package org.example.classpiserver.service;

import org.example.classpiserver.dto.CourseRequest;
import org.example.classpiserver.entity.*;
import org.example.classpiserver.mapper.UserMapper;
import org.example.classpiserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean addAccount(Accounts account) {
        account.setEmail_or_phone("yes");
        account.setStatus_number("12857");
         account.setPassword(encryptPassword(account.getPassword()));
         if(userMapper.selectAccountByAccount(account.getAccount()) != null) {
             return false;
         }
        return userMapper.addUser(account);
    }

    @Override
    public boolean changePassword(String password, String account) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("密码长度至少8位，包含字母和数字");
        }
        return userMapper.changePassword(password, account);
    }

    @Override
    public Accounts login(String account,String password) {
        return userMapper.selectAccount(account, password);
    }

    @Override
    public boolean selectAccountByAccount(String account) {
        return userMapper.selectAccountByAccount(account) != null;
    }

    @Override
    public List<Long> getCourseIdByAccount(String account) {
        return userMapper.getCourseIdByAccount(account);
    }

    @Override
    public Accounts getAccount(String account) {
       return userMapper.getAccount(account);
    }

    @Override
    public Course getCourseByCode(String account, String code) {
        if (userMapper.getCourseByCode(code) != null){
            userMapper.createCourse(account,userMapper.getCourseByCode(code).getId());
        }
        return userMapper.getCourseByCode(code);
    }
    @Override
    public void addCourse(String account,Long class_id) {
        userMapper.createCourse(account,class_id);
    }

    @Override
    public List<Course> getCourseByCourseId(List<Long> courseId) {
        List<Course> courseList = new ArrayList<>();
        for (Long id : courseId) {
            courseList.add(userMapper.getCourseByCourseId(id));
        }
        return courseList;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    private String encryptPassword(String password) {
        return password;
    }

    @Override
    public boolean togglePinCourse(Long courseId, Boolean isPinned) {
        Course course = userMapper.getCourseByCourseId(courseId);
        if (course == null) return false;
        course.setIs_pinned(isPinned);
        return userMapper.updateCourse(isPinned,courseId);
    }

    @Override
    public String getAccountName(String account){
        return userMapper.getAccountName(account);
    }

    @Override
    public boolean addCourse(CourseRequest course){
        return userMapper.addCourse(course);
    }

    @Override
    public boolean addTeacherCourse(String account){
        try {
            for (Long id : userMapper.getCourseIdByTeacherAccount(account)) {
                userMapper.createCourse(account, id);
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public Course getCourseById(Long courseId) {
        return userMapper.getCourseByCourseId(courseId);
    }

    @Override
    public Integer getCountByCourseId(Long id){
        return userMapper.getCountByCourseId(id);
    }

    @Override
    public boolean deleteCourse(Long courseId) {
        return userMapper.deleteCourse(courseId);
    }

    @Override
    public boolean addHomework(Homework homework, Integer class_id) {
        try {
            userMapper.addHomework(homework);
            Integer homeworkId = userMapper.getLastInsertId();
            userMapper.addCourses_homework(class_id, homeworkId);
            return true;
        } catch (Exception e) {
            System.err.println(userMapper.getLastInsertId());
            System.err.println("添加作业失败");
            System.err.println(e);
            return false;
        }
    }

    @Override
    public Integer getCountByClassId(Integer class_id){
        System.err.println(class_id);
        System.err.println(userMapper.getCountByClassId(class_id));
        return userMapper.getCountByClassId(class_id);
    }

    @Override
    public List<Homework> getHomeworkByClassId(Integer class_id) {
        List<Homework> homeworkList = new ArrayList<>();
        for (Integer homework_id : userMapper.getCourseIdByClassId(class_id)){
            homeworkList.add(userMapper.getHomework(homework_id));
        }
        return homeworkList;
    }

    @Override
    public Homework getHomeworkById(Integer homework_id) {
        return userMapper.getHomework(homework_id);
    }

    @Override
    public String getAccountStatus(String account) {
        return userMapper.getAccountStatus(account);
    }

    @Override
    public List<Content> getContentById(Long content_id) {
        return userMapper.getContentByContentId(content_id);
    }

    @Override
    public boolean setContentScore(int newScore,Long content_id,String account) {
        return userMapper.setContentScore(newScore,content_id,account);
    }

    @Override
    public boolean addContent(Content content) {
        return userMapper.addContent(content);
    }
}
