package org.example.classpiserver.mapper.activity;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.CourseActivity;
import org.example.classpiserver.entity.CourseActivityReply;

import java.util.List;

@Mapper
public interface ActivityMapper {

    @Insert("insert into course_activity (class_id, type, title, content, attachment_url, attachment_name, start_time, deadline, creator_account, publish_status, interaction_kind, interaction_options) " +
            "values (#{activity.class_id}, #{activity.type}, #{activity.title}, #{activity.content}, #{activity.attachment_url}, #{activity.attachment_name}, #{activity.start_time}, #{activity.deadline}, #{activity.creator_account}, #{activity.publish_status}, #{activity.interaction_kind}, #{activity.interaction_options})")
    @Options(useGeneratedKeys = true, keyProperty = "activity.id")
    boolean addCourseActivity(@Param("activity") CourseActivity activity);

    @Update("update course_activity set title = #{title}, content = #{content}, start_time = #{start_time}, deadline = #{deadline}, publish_status = #{publish_status} where id = #{id} and type = 'test'")
    boolean updateCourseTest(@Param("id") Long id, @Param("title") String title, @Param("content") String content,
                             @Param("start_time") String start_time, @Param("deadline") String deadline,
                             @Param("publish_status") String publish_status);

    @Select("select ca.*, a.name as creator_name, " +
            "(select count(*) from course_test_submission s where s.activity_id = ca.id) as reply_count, " +
            "(select count(*) from course_test_question q where q.activity_id = ca.id and q.question_type = 'choice') as choice_count, " +
            "(select count(*) from course_test_question q where q.activity_id = ca.id and q.question_type = 'short') as short_count " +
            "from course_activity ca " +
            "left join accounts a on ca.creator_account = a.account " +
            "where ca.class_id = #{class_id} and ca.type = 'test' order by ca.create_time desc")
    List<CourseActivity> getCourseTestsByClassId(@Param("class_id") Integer class_id);

    @Select("select ca.*, a.name as creator_name, " +
            "(select count(*) from course_test_submission s where s.activity_id = ca.id) as reply_count, " +
            "(select count(*) from course_test_question q where q.activity_id = ca.id and q.question_type = 'choice') as choice_count, " +
            "(select count(*) from course_test_question q where q.activity_id = ca.id and q.question_type = 'short') as short_count " +
            "from course_activity ca " +
            "left join accounts a on ca.creator_account = a.account " +
            "where ca.class_id = #{class_id} and ca.type = 'test' and ca.publish_status = 'published' order by ca.create_time desc")
    List<CourseActivity> getPublishedCourseTestsByClassId(@Param("class_id") Integer class_id);

    @Select("select ca.*, a.name as creator_name, " +
            "(select count(distinct r.account) from course_interaction_response r where r.activity_id = ca.id) as reply_count, " +
            "0 as choice_count, 0 as short_count " +
            "from course_activity ca " +
            "left join accounts a on ca.creator_account = a.account " +
            "where ca.class_id = #{class_id} and ca.type = 'interaction' order by ca.create_time desc")
    List<CourseActivity> getCourseInteractionsByClassId(@Param("class_id") Integer class_id);

    @Select("select ca.*, a.name as creator_name, " +
            "(select count(*) from course_activity_reply r where r.activity_id = ca.id) as reply_count, " +
            "0 as choice_count, 0 as short_count " +
            "from course_activity ca " +
            "left join accounts a on ca.creator_account = a.account " +
            "where ca.class_id = #{class_id} and ca.type = #{type} and ca.type <> 'interaction' order by ca.create_time desc")
    List<CourseActivity> getCourseActivitiesByType(@Param("class_id") Integer class_id, @Param("type") String type);

    @Select("select ca.*, a.name as creator_name, " +
            "(select count(*) from course_test_submission s where s.activity_id = ca.id) as reply_count, " +
            "(select count(*) from course_test_question q where q.activity_id = ca.id and q.question_type = 'choice') as choice_count, " +
            "(select count(*) from course_test_question q where q.activity_id = ca.id and q.question_type = 'short') as short_count " +
            "from course_activity ca left join accounts a on ca.creator_account = a.account where ca.id = #{id}")
    CourseActivity getCourseActivityById(@Param("id") Long id);

    @Select("select r.*, a.name as creator_name, a.avatar_url from course_activity_reply r " +
            "inner join accounts a on r.account = a.account " +
            "where r.activity_id = #{activity_id} order by r.create_time asc")
    List<CourseActivityReply> getActivityReplies(@Param("activity_id") Long activity_id);

    @Insert("insert into course_activity_reply (activity_id, account, content, attachment_url, attachment_name) values (#{activity_id}, #{account}, #{content}, #{attachment_url}, #{attachment_name})")
    boolean addActivityReply(CourseActivityReply reply);

    @Select("select count(*) from course_activity_reply where activity_id = #{activity_id} and account = #{account}")
    Integer countActivityReplyByAccount(@Param("activity_id") Long activity_id, @Param("account") String account);

    @Select("select count(*) from course_activity where class_id = #{class_id} and type = #{type}")
    Integer countCourseActivitiesByType(@Param("class_id") Integer class_id, @Param("type") String type);

    @Update("update course_activity set content = #{content}, interaction_options = #{interaction_options} where id = #{id}")
    boolean updateCourseInteractionState(@Param("id") Long id, @Param("content") String content,
                                         @Param("interaction_options") String interaction_options);

    @Delete("delete from course_activity where id = #{id}")
    boolean deleteActivityById(@Param("id") Long id);
}
