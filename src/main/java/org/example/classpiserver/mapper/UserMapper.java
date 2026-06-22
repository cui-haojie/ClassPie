package org.example.classpiserver.mapper;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.dto.CourseRequest;
import org.example.classpiserver.entity.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from accounts where account = #{account} and password = #{password}")
    Accounts selectAccount(@Param("account") String account,@Param("password") String password);

    @Insert("insert into accounts (account, name, status, password, mechanism, status_number,email_or_phone) " +
            "values (#{account}, #{name}, #{status}, #{password}, #{mechanism}, #{status_number} ,#{email_or_phone})")
    boolean addUser(Accounts account);
    // 检验是否已有账户
    @Select("select * from accounts where account = #{account}")
    Integer selectAccountByAccount(@Param("account") String account);

    @Update("update accounts set password = #{newPassword} where account = #{account}")
    boolean changePassword(@Param("newPassword") String newPassword,@Param("account") String account);

    @Insert("insert into courses (teacher_account, class_time, class_name, selected_classes) VALUES " +
            "(#{course.teacher_account}, #{course.class_time}, #{course.class_name}, #{course.selected_classes})")
    boolean addCourse(@Param("course") CourseRequest course);

    @Delete("delete from account_course where class_id = #{id}")
    boolean deleteCourse(@Param("id") Long id);

    @Select("select class_id from account_course where account = #{account}")
    List<Long> getCourseIdByAccount(@Param("account") String account);

    @Insert("insert into account_course (account, class_id) values (#{account}, #{class_id})")
    Course insertCourse(Account_course account_course);

    @Select("SELECT * FROM accounts where account = #{account} limit 1")
    Accounts getAccount(@Param("account") String account);

    @Select("select * from courses where code = #{code}")
    Course getCourseByCode(@Param("code") String code);

    @Insert("insert into account_course (account,class_id) values (#{account}, #{class_id})")
    void createCourse(@Param("account") String account, @Param("class_id") Long class_id);

    @Select("select * from courses where id = #{id}")
    Course getCourseByCourseId(@Param("id") Long id);

    @Update("update courses set is_pinned = #{is_pinned} where id = #{id}")
    boolean updateCourse(@Param("is_pinned") boolean is_pinned, @Param("id") Long id);

    @Select("select name from accounts where account = #{account}")
    String getAccountName(@Param("account") String account);

    @Select("select id from courses where teacher_account = #{account}")
    List<Long> getCourseIdByTeacherAccount(@Param("account") String account);

    @Select("select count(*) from t_class.account_course where class_id = #{id}")
    Integer getCountByCourseId(@Param("id") Long id);

    @Insert("insert into homework (homework_id,content_id,name,deadline,type,isCorrect,score,details)" +
            " values (#{homework.homework_id}, #{homework.content_id}, #{homework.name}, #{homework.deadline}, #{homework.type}, #{homework.isCorrect}, #{homework.score}, #{homework.details})")
    boolean addHomework(@Param("homework") Homework homework);

    @Insert("insert into courses_homework (class_id , homework_id) values (#{class_id}, #{homework_id})")
    boolean addCourses_homework(@Param("class_id")Integer class_id,@Param("homework_id")Integer homework_id);

    @Select("SELECT LAST_INSERT_ID() AS homework_id")
    Integer getLastInsertId();

    @Select("select count(*) from courses_homework where class_id = #{class_id}")
    Integer getCountByClassId(@Param("class_id") Integer class_id);

    @Select("select * from homework where homework_id = #{homework_id}")
    Homework getHomework(@Param("homework_id") Integer homework_id);

    @Select("select homework_id from courses_homework where class_id = #{class_id}")
    List<Integer> getCourseIdByClassId(@Param("class_id") Integer class_id);

    @Select("select status from accounts where account = #{account}")
    String getAccountStatus(@Param("account") String account);

    @Select("select * from content where content_id = #{content_id}")
    List<Content> getContentByContentId(@Param("content_id") Long content_id);

    @Update("UPDATE content set score = #{newScore} where content_id = #{conten_id} and account = #{account}")
    boolean setContentScore(@Param("newScore") int newScore,@Param("content_id") Long content_id,@Param("account") String account);

    @Insert("insert into content (content_id,account,score,details) values (#{content.content_id}, #{content.account}, #{content.score}, #{content.details});")
    boolean addContent(@Param("content") Content content);
}
