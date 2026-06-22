package org.example.classpiserver.service;

import org.example.classpiserver.dto.*;
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
    public List<Long> getArchivedCourseIdByAccount(String account) {
        return userMapper.getArchivedCourseIdByAccount(account);
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
        return userMapper.updateCoursePin(isPinned,courseId);
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
    public boolean leaveCourse(String account, Long courseId) {
        return userMapper.leaveCourse(courseId, account);
    }

    @Override
    public boolean updateCourseInfo(CourseUpdateRequest request) {
        if (request == null || request.getId() == null) {
            return false;
        }
        return userMapper.updateCourseInfo(request);
    }

    @Override
    public boolean archiveCourse(ArchiveCourseRequest request) {
        if (request == null || request.getAccount() == null || request.getClass_id() == null) {
            return false;
        }
        return userMapper.setCourseArchived(
                request.getAccount(),
                request.getClass_id(),
                request.isArchived() ? 1 : 0
        );
    }

    @Override
    public List<CourseMember> getCourseMembers(Long classId) {
        return userMapper.getCourseMembers(classId);
    }

    @Override
    public boolean addHomework(Homework homework, Integer class_id) {
        try {
            userMapper.addHomework(homework);
            Integer homeworkId = userMapper.getLastInsertId();
            userMapper.setHomeworkContentId(homeworkId, homeworkId);
            userMapper.addCourses_homework(class_id, homeworkId);
            notifyHomeworkPublished(class_id, homeworkId, homework.getName());
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

    @Override
    public boolean updateAccount(Accounts account) {
        if (account == null || account.getAccount() == null || account.getAccount().isBlank()) {
            return false;
        }
        Accounts existing = userMapper.getAccount(account.getAccount());
        if (existing == null) {
            return false;
        }
        if (account.getName() != null) {
            existing.setName(account.getName());
        }
        if (account.getMechanism() != null) {
            existing.setMechanism(account.getMechanism());
        }
        if (account.getEmail_or_phone() != null) {
            existing.setEmail_or_phone(account.getEmail_or_phone());
        }
        if (account.getStatus_number() != null) {
            existing.setStatus_number(account.getStatus_number());
        }
        if (account.getStatus() != null) {
            existing.setStatus(account.getStatus());
        }
        return userMapper.updateAccount(existing);
    }

    @Override
    public List<Notification> getNotifications(String account) {
        return userMapper.getNotifications(account);
    }

    @Override
    public Integer getUnreadNotificationCount(String account) {
        return userMapper.getUnreadNotificationCount(account);
    }

    @Override
    public boolean markNotificationRead(Integer id, String account) {
        return userMapper.markNotificationRead(id, account);
    }

    @Override
    public boolean remindHomework(RemindHomeworkRequest request) {
        if (request == null || request.getHomework_id() == null || request.getClass_id() == null) {
            return false;
        }
        Homework homework = userMapper.getHomework(request.getHomework_id());
        if (homework == null) {
            return false;
        }
        Long contentId = homework.getContent_id() > 0 ? (long) homework.getContent_id() : request.getHomework_id().longValue();
        List<String> accounts = userMapper.getUnsubmittedAccounts(request.getClass_id(), contentId);
        if (accounts.isEmpty()) {
            return false;
        }
        for (String studentAccount : accounts) {
            Notification notification = new Notification();
            notification.setAccount(studentAccount);
            notification.setClass_id(request.getClass_id());
            notification.setHomework_id(request.getHomework_id());
            notification.setType("remind");
            notification.setMessage("老师催交作业：" + homework.getName());
            userMapper.addNotification(notification);
        }
        return true;
    }

    private void notifyHomeworkPublished(Integer classId, Integer homeworkId, String homeworkName) {
        List<CourseMember> members = userMapper.getCourseMembers(classId.longValue());
        for (CourseMember member : members) {
            if ("老师".equals(member.getStatus())) {
                continue;
            }
            Notification notification = new Notification();
            notification.setAccount(member.getAccount());
            notification.setClass_id(classId);
            notification.setHomework_id(homeworkId);
            notification.setType("homework");
            notification.setMessage("新作业发布：" + homeworkName);
            userMapper.addNotification(notification);
        }
    }
}
