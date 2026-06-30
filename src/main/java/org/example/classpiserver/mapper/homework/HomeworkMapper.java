package org.example.classpiserver.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.Content;
import org.example.classpiserver.entity.Homework;

import java.util.List;

@Mapper
public interface HomeworkMapper {

    @Insert("insert into homework (homework_id,content_id,name,deadline,type,isCorrect,score,details,attachment_url,attachment_name)" +
            " values (#{homework.homework_id}, #{homework.content_id}, #{homework.name}, #{homework.deadline}, #{homework.type}, #{homework.isCorrect}, #{homework.score}, #{homework.details}, #{homework.attachment_url}, #{homework.attachment_name})")
    boolean addHomework(@Param("homework") Homework homework);

    @Insert("insert into courses_homework (class_id , homework_id) values (#{class_id}, #{homework_id})")
    boolean addCourses_homework(@Param("class_id") Integer class_id, @Param("homework_id") Integer homework_id);

    @Select("SELECT LAST_INSERT_ID() AS homework_id")
    Integer getLastInsertId();

    @Select("select count(*) from courses_homework where class_id = #{class_id}")
    Integer getCountByClassId(@Param("class_id") Integer class_id);

    @Select("select * from homework where homework_id = #{homework_id}")
    Homework getHomework(@Param("homework_id") Integer homework_id);

    @Select("select * from homework where homework_id = #{id} or content_id = #{id} limit 1")
    Homework getHomeworkByContentOrId(@Param("id") Long id);

    @Select("select homework_id from courses_homework where class_id = #{class_id}")
    List<Integer> getHomeworkIdsByClassId(@Param("class_id") Integer class_id);

    @Update("update homework set content_id = #{content_id} where homework_id = #{homework_id}")
    boolean setHomeworkContentId(@Param("homework_id") Integer homework_id, @Param("content_id") Integer content_id);

    @Select("select * from content where content_id = #{content_id}")
    List<Content> getContentByContentId(@Param("content_id") Long content_id);

    @Update("UPDATE content set score = #{newScore}, is_graded = 1 where content_id = #{content_id} and account = #{account}")
    boolean setContentScore(@Param("newScore") int newScore, @Param("content_id") Long content_id, @Param("account") String account);

    @Insert("insert into content (content_id,account,score,details,attachment_url,attachment_name,is_graded) values (#{content.content_id}, #{content.account}, #{content.score}, #{content.details}, #{content.attachment_url}, #{content.attachment_name}, #{content.is_graded});")
    boolean addContent(@Param("content") Content content);

    @Select("select count(*) from content where content_id = #{content_id} and account = #{account}")
    Integer countContentSubmission(@Param("content_id") Long content_id, @Param("account") String account);

    @Select("select * from content where content_id = #{content_id} and account = #{account} limit 1")
    Content getContentByAccount(@Param("content_id") Long content_id, @Param("account") String account);

    @Select("SELECT ac.account FROM account_course ac LEFT JOIN content c ON c.account = ac.account AND c.content_id = #{contentId} WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND c.account IS NULL")
    List<String> getUnsubmittedAccounts(@Param("classId") Integer classId, @Param("contentId") Long contentId);

    @Select("SELECT COUNT(*) FROM content c INNER JOIN accounts a ON c.account = a.account WHERE c.content_id = #{contentId} AND a.status = '学生' AND c.is_graded = 1")
    Integer countGradedSubmissions(@Param("contentId") Long contentId);

    @Select("SELECT COUNT(*) FROM content c INNER JOIN accounts a ON c.account = a.account WHERE c.content_id = #{contentId} AND a.status = '学生' AND c.is_graded = 0")
    Integer countUngradedSubmissions(@Param("contentId") Long contentId);

    @Select("SELECT COUNT(*) FROM account_course ac INNER JOIN accounts a ON ac.account = a.account LEFT JOIN content c ON c.account = ac.account AND c.content_id = #{contentId} WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND a.status = '学生' AND c.account IS NULL")
    Integer countUnsubmittedStudents(@Param("classId") Long classId, @Param("contentId") Long contentId);

    @Select("SELECT COUNT(*) FROM account_course ac INNER JOIN accounts a ON ac.account = a.account WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND a.status = '学生'")
    Integer countStudentMembersByClass(@Param("classId") Long classId);

    @Delete("delete from courses_homework where class_id = #{class_id} and homework_id = #{homework_id}")
    boolean deleteCourseHomeworkLink(@Param("class_id") Integer class_id, @Param("homework_id") Integer homework_id);

    @Delete("delete from homework where homework_id = #{homework_id}")
    boolean deleteHomeworkById(@Param("homework_id") Integer homework_id);
}
