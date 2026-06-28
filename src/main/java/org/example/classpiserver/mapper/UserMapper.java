package org.example.classpiserver.mapper;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.dto.CourseRequest;
import org.example.classpiserver.dto.CourseUpdateRequest;
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

    @Select("SELECT * FROM accounts where account = #{account} limit 1")
    Accounts getAccount(@Param("account") String account);

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

    @Select("select name from accounts where account = #{account}")
    String getAccountName(@Param("account") String account);

    @Select("select id from courses where teacher_account = #{account}")
    List<Long> getCourseIdByTeacherAccount(@Param("account") String account);

    @Select("select count(*) from t_class.account_course where class_id = #{id}")
    Integer getCountByCourseId(@Param("id") Long id);

    @Insert("insert into homework (homework_id,content_id,name,deadline,type,isCorrect,score,details,attachment_url,attachment_name)" +
            " values (#{homework.homework_id}, #{homework.content_id}, #{homework.name}, #{homework.deadline}, #{homework.type}, #{homework.isCorrect}, #{homework.score}, #{homework.details}, #{homework.attachment_url}, #{homework.attachment_name})")
    boolean addHomework(@Param("homework") Homework homework);

    @Insert("insert into courses_homework (class_id , homework_id) values (#{class_id}, #{homework_id})")
    boolean addCourses_homework(@Param("class_id")Integer class_id,@Param("homework_id")Integer homework_id);

    @Select("SELECT LAST_INSERT_ID() AS homework_id")
    Integer getLastInsertId();

    @Select("select count(*) from courses_homework where class_id = #{class_id}")
    Integer getCountByClassId(@Param("class_id") Integer class_id);

    @Select("select * from homework where homework_id = #{homework_id}")
    Homework getHomework(@Param("homework_id") Integer homework_id);

    @Select("select * from homework where homework_id = #{id} or content_id = #{id} limit 1")
    Homework getHomeworkByContentOrId(@Param("id") Long id);

    @Select("select homework_id from courses_homework where class_id = #{class_id}")
    List<Integer> getCourseIdByClassId(@Param("class_id") Integer class_id);

    @Update("update courses set class_name = #{class_name}, class_time = #{class_time}, selected_classes = #{selected_classes}, semester = #{semester} where id = #{id} and teacher_account = #{teacher_account}")
    boolean updateCourseInfo(CourseUpdateRequest course);

    @Update("update accounts set name = #{name}, mechanism = #{mechanism}, email_or_phone = #{email_or_phone}, status_number = #{status_number}, status = #{status} where account = #{account}")
    boolean updateAccount(Accounts account);

    @Update("update account_course set is_archived = #{archived} where account = #{account} and class_id = #{class_id}")
    boolean setCourseArchived(@Param("account") String account, @Param("class_id") Long class_id, @Param("archived") int archived);

    @Update("update account_course set is_archived = #{archived} where class_id = #{class_id}")
    boolean setCourseArchivedForClass(@Param("class_id") Long class_id, @Param("archived") int archived);

    @Select("SELECT a.account, a.name, a.status, a.status_number FROM accounts a INNER JOIN account_course ac ON a.account = ac.account WHERE ac.class_id = #{classId} AND ac.is_archived = 0")
    List<CourseMember> getCourseMembers(@Param("classId") Long classId);

    @Select("SELECT ac.account FROM account_course ac LEFT JOIN content c ON c.account = ac.account AND c.content_id = #{contentId} WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND c.account IS NULL")
    List<String> getUnsubmittedAccounts(@Param("classId") Integer classId, @Param("contentId") Long contentId);

    @Insert("insert into notification (account, class_id, homework_id, type, message, is_read) values (#{account}, #{class_id}, #{homework_id}, #{type}, #{message}, 0)")
    boolean addNotification(Notification notification);

    @Select("select * from notification where account = #{account} order by id desc limit 50")
    List<Notification> getNotifications(@Param("account") String account);

    @Update("update notification set is_read = 1 where id = #{id} and account = #{account}")
    boolean markNotificationRead(@Param("id") Integer id, @Param("account") String account);

    @Update("update notification set is_read = 1 where account = #{account} and is_read = 0")
    boolean markAllNotificationsRead(@Param("account") String account);

    @Select("select count(*) from notification where account = #{account} and is_read = 0")
    Integer getUnreadNotificationCount(@Param("account") String account);

    @Update("update homework set content_id = #{content_id} where homework_id = #{homework_id}")
    boolean setHomeworkContentId(@Param("homework_id") Integer homework_id, @Param("content_id") Integer content_id);

    @Select("select status from accounts where account = #{account}")
    String getAccountStatus(@Param("account") String account);

    @Select("select * from content where content_id = #{content_id}")
    List<Content> getContentByContentId(@Param("content_id") Long content_id);

    @Update("UPDATE content set score = #{newScore}, is_graded = 1 where content_id = #{content_id} and account = #{account}")
    boolean setContentScore(@Param("newScore") int newScore,@Param("content_id") Long content_id,@Param("account") String account);

    @Insert("insert into content (content_id,account,score,details,attachment_url,attachment_name,is_graded) values (#{content.content_id}, #{content.account}, #{content.score}, #{content.details}, #{content.attachment_url}, #{content.attachment_name}, #{content.is_graded});")
    boolean addContent(@Param("content") Content content);

    @Select("SELECT COUNT(*) FROM account_course ac INNER JOIN accounts a ON ac.account = a.account WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND a.status = '学生'")
    Integer countStudentMembersByClass(@Param("classId") Long classId);

    @Select("SELECT COUNT(*) FROM content c INNER JOIN accounts a ON c.account = a.account WHERE c.content_id = #{contentId} AND a.status = '学生' AND c.is_graded = 1")
    Integer countGradedSubmissions(@Param("contentId") Long contentId);

    @Select("SELECT COUNT(*) FROM content c INNER JOIN accounts a ON c.account = a.account WHERE c.content_id = #{contentId} AND a.status = '学生' AND c.is_graded = 0")
    Integer countUngradedSubmissions(@Param("contentId") Long contentId);

    @Select("SELECT COUNT(*) FROM account_course ac INNER JOIN accounts a ON ac.account = a.account LEFT JOIN content c ON c.account = ac.account AND c.content_id = #{contentId} WHERE ac.class_id = #{classId} AND ac.is_archived = 0 AND a.status = '学生' AND c.account IS NULL")
    Integer countUnsubmittedStudents(@Param("classId") Long classId, @Param("contentId") Long contentId);

    @Select("select count(*) from content where content_id = #{content_id} and account = #{account}")
    Integer countContentSubmission(@Param("content_id") Long content_id, @Param("account") String account);

    @Update("update accounts set avatar_url = #{avatar_url} where account = #{account}")
    boolean updateAvatarUrl(@Param("account") String account, @Param("avatar_url") String avatar_url);

    @Insert("insert into school_class (name, mechanism, teacher_account) values (#{name}, #{mechanism}, #{teacher_account})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean insertSchoolClass(SchoolClass schoolClass);

    @Select("select * from school_class order by id desc")
    List<SchoolClass> listSchoolClasses();

    @Select("select * from school_class where id = #{id}")
    SchoolClass getSchoolClassById(@Param("id") Integer id);

    @Select("select count(*) from student_class where account = #{account} and school_class_id = #{school_class_id}")
    Integer countStudentInClass(@Param("account") String account, @Param("school_class_id") Integer school_class_id);

    @Insert("insert into student_class (account, school_class_id) values (#{account}, #{school_class_id})")
    boolean insertStudentClass(@Param("account") String account, @Param("school_class_id") Integer school_class_id);

    @Delete("delete from student_class where account = #{account}")
    boolean deleteStudentClassesByAccount(@Param("account") String account);

    @Select("select account from student_class where school_class_id = #{school_class_id}")
    List<String> getStudentAccountsBySchoolClass(@Param("school_class_id") Integer school_class_id);

    @Select("select id from courses where school_class_id = #{school_class_id}")
    List<Long> getCourseIdsBySchoolClassLegacy(@Param("school_class_id") Integer school_class_id);

    @Select("select course_id from course_school_class where school_class_id = #{school_class_id}")
    List<Long> getCourseIdsBySchoolClassLink(@Param("school_class_id") Integer school_class_id);

    @Insert("insert into course_school_class (course_id, school_class_id) values (#{course_id}, #{school_class_id})")
    boolean insertCourseSchoolClass(@Param("course_id") Long course_id, @Param("school_class_id") Integer school_class_id);

    @Select("select count(*) from account_course where account = #{account} and class_id = #{class_id}")
    Integer countAccountInCourse(@Param("account") String account, @Param("class_id") Long class_id);

    @Select("select sc.* from student_class st join school_class sc on st.school_class_id = sc.id where st.account = #{account} limit 1")
    SchoolClass getSchoolClassByStudentAccount(@Param("account") String account);

    @Select("select sc.* from student_class st join school_class sc on st.school_class_id = sc.id where st.account = #{account} order by sc.id")
    List<SchoolClass> getSchoolClassesByStudentAccount(@Param("account") String account);

    @Insert("insert into course_activity (class_id, type, title, content, attachment_url, attachment_name, start_time, deadline, creator_account, publish_status) " +
            "values (#{activity.class_id}, #{activity.type}, #{activity.title}, #{activity.content}, #{activity.attachment_url}, #{activity.attachment_name}, #{activity.start_time}, #{activity.deadline}, #{activity.creator_account}, #{activity.publish_status})")
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
            "(select count(*) from course_activity_reply r where r.activity_id = ca.id) as reply_count, " +
            "0 as choice_count, 0 as short_count " +
            "from course_activity ca " +
            "left join accounts a on ca.creator_account = a.account " +
            "where ca.class_id = #{class_id} and ca.type = #{type} order by ca.create_time desc")
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

    @Insert("insert into course_test_question (activity_id, question_type, stem, option_a, option_b, option_c, option_d, correct_option, score, sort_order) " +
            "values (#{activity_id}, #{question_type}, #{stem}, #{option_a}, #{option_b}, #{option_c}, #{option_d}, #{correct_option}, #{score}, #{sort_order})")
    boolean addTestQuestion(TestQuestion question);

    @Select("select * from course_test_question where activity_id = #{activity_id} order by sort_order asc, id asc")
    List<TestQuestion> getTestQuestionsByActivityId(@Param("activity_id") Long activity_id);

    @Delete("delete from course_test_question where activity_id = #{activity_id}")
    boolean deleteTestQuestionsByActivityId(@Param("activity_id") Long activity_id);

    @Select("select * from course_test_submission where activity_id = #{activity_id} and account = #{account} limit 1")
    TestSubmission getTestSubmissionByAccount(@Param("activity_id") Long activity_id, @Param("account") String account);

    @Insert("insert into course_test_submission (activity_id, account) values (#{activity_id}, #{account})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean addTestSubmission(TestSubmission submission);

    @Insert("insert into course_test_answer (submission_id, question_id, answer) values (#{submission_id}, #{question_id}, #{answer})")
    boolean addTestAnswer(@Param("submission_id") Long submission_id, @Param("question_id") Long question_id, @Param("answer") String answer);

    @Select("select question_id, answer from course_test_answer where submission_id = #{submission_id}")
    List<java.util.Map<String, Object>> getTestAnswersBySubmissionId(@Param("submission_id") Long submission_id);
}
