package org.example.classpiserver.mapper.course;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.dto.course.CourseRequest;
import org.example.classpiserver.dto.course.CourseUpdateRequest;
import org.example.classpiserver.entity.Account_course;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseMember;

import java.util.List;

@Mapper
public interface CourseMapper {

    @Insert("insert into courses (teacher_account, class_time, class_name, selected_classes, code, school_class_id, semester) VALUES " +
            "(#{course.teacher_account}, #{course.class_time}, #{course.class_name}, #{course.selected_classes}, #{course.code}, #{course.school_class_id}, #{course.semester})")
    boolean addCourse(@Param("course") CourseRequest course);

    @Select("SELECT LAST_INSERT_ID()")
    Long getLastInsertCourseId();

    @Delete("delete from account_course where class_id = #{id} and account = #{account}")
    boolean leaveCourse(@Param("id") Long id, @Param("account") String account);

    @Select("select class_id from account_course where account = #{account} and is_archived = 0 order by sort_order asc, class_id asc")
    List<Long> getCourseIdByAccount(@Param("account") String account);

    @Select("select class_id from account_course where account = #{account} and is_archived = 1 order by sort_order asc, class_id asc")
    List<Long> getArchivedCourseIdByAccount(@Param("account") String account);

    @Insert("insert into account_course (account, class_id) values (#{account}, #{class_id})")
    Course insertCourse(Account_course account_course);

    @Select("select * from courses where code = #{code}")
    Course getCourseByCode(@Param("code") String code);

    @Insert("insert into account_course (account,class_id,sort_order) values (#{account}, #{class_id}, #{sort_order})")
    void createCourseWithOrder(@Param("account") String account, @Param("class_id") Long class_id, @Param("sort_order") int sort_order);

    @Select("select COALESCE(MAX(sort_order), -1) from account_course where account = #{account}")
    Integer getMaxSortOrder(@Param("account") String account);

    @Update("update account_course set sort_order = #{sort_order} where account = #{account} and class_id = #{class_id}")
    boolean updateCourseSortOrder(@Param("account") String account, @Param("class_id") Long class_id, @Param("sort_order") int sort_order);

    @Insert("insert into account_course (account,class_id) values (#{account}, #{class_id})")
    void createCourse(@Param("account") String account, @Param("class_id") Long class_id);

    @Select("select * from courses where id = #{id}")
    Course getCourseByCourseId(@Param("id") Long id);

    @Update("update courses set is_pinned = #{is_pinned} where id = #{class_id}")
    boolean updateCoursePin(@Param("is_pinned") boolean is_pinned, @Param("account") String account, @Param("class_id") Long class_id);

    @Select("select is_pinned from courses where id = #{class_id}")
    Integer getCoursePinStatus(@Param("account") String account, @Param("class_id") Long class_id);

    @Select("select id from courses where teacher_account = #{account}")
    List<Long> getCourseIdByTeacherAccount(@Param("account") String account);

    @Select("select count(*) from t_class.account_course where class_id = #{id}")
    Integer getCountByCourseId(@Param("id") Long id);

    @Update("update courses set class_name = #{class_name}, class_time = #{class_time}, selected_classes = #{selected_classes}, semester = #{semester}, school_class_id = #{school_class_id} where id = #{id} and teacher_account = #{teacher_account}")
    boolean updateCourseInfo(CourseUpdateRequest course);

    @Select("select school_class_id from course_school_class where course_id = #{course_id}")
    List<Integer> getSchoolClassIdsByCourseId(@Param("course_id") Long course_id);

    @Delete("delete from course_school_class where course_id = #{course_id}")
    boolean deleteCourseSchoolClassLinks(@Param("course_id") Long course_id);

    @Update("update account_course set is_archived = #{archived} where account = #{account} and class_id = #{class_id}")
    boolean setCourseArchived(@Param("account") String account, @Param("class_id") Long class_id, @Param("archived") int archived);

    @Update("update account_course set is_archived = #{archived} where class_id = #{class_id}")
    boolean setCourseArchivedForClass(@Param("class_id") Long class_id, @Param("archived") int archived);

    @Select("SELECT a.account, a.name, a.status, a.status_number FROM accounts a INNER JOIN account_course ac ON a.account = ac.account WHERE ac.class_id = #{classId} AND ac.is_archived = 0")
    List<CourseMember> getCourseMembers(@Param("classId") Long classId);

    @Select("select count(*) from account_course where account = #{account} and class_id = #{class_id}")
    Integer countAccountInCourse(@Param("account") String account, @Param("class_id") Long class_id);

    @Insert("insert into course_school_class (course_id, school_class_id) values (#{course_id}, #{school_class_id})")
    boolean insertCourseSchoolClass(@Param("course_id") Long course_id, @Param("school_class_id") Integer school_class_id);

    @Select("SELECT a.account, a.name, a.status, a.status_number FROM accounts a " +
            "INNER JOIN account_course ac ON a.account = ac.account " +
            "WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND a.status = '学生'")
    List<CourseMember> getCourseStudents(@Param("classId") Long classId);
}
