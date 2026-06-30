package org.example.classpiserver.mapper.test;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.TestAnswer;
import org.example.classpiserver.entity.TestQuestion;
import org.example.classpiserver.entity.TestSubmission;

import java.util.List;

@Mapper
public interface TestMapper {

    @Insert("insert into course_test_question (activity_id, question_type, stem, option_a, option_b, option_c, option_d, correct_option, score, sort_order, stem_image_url) " +
            "values (#{activity_id}, #{question_type}, #{stem}, #{option_a}, #{option_b}, #{option_c}, #{option_d}, #{correct_option}, #{score}, #{sort_order}, #{stem_image_url})")
    boolean addTestQuestion(TestQuestion question);

    @Select("select * from course_test_question where activity_id = #{activity_id} order by sort_order asc, id asc")
    List<TestQuestion> getTestQuestionsByActivityId(@Param("activity_id") Long activity_id);

    @Delete("delete from course_test_question where activity_id = #{activity_id}")
    boolean deleteTestQuestionsByActivityId(@Param("activity_id") Long activity_id);

    @Select("select * from course_test_submission where activity_id = #{activity_id} and account = #{account} limit 1")
    TestSubmission getTestSubmissionByAccount(@Param("activity_id") Long activity_id, @Param("account") String account);

    @Insert("insert into course_test_submission (activity_id, account, auto_score, manual_score, total_score, max_score, is_fully_graded) " +
            "values (#{activity_id}, #{account}, #{auto_score}, #{manual_score}, #{total_score}, #{max_score}, #{is_fully_graded})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean addTestSubmission(TestSubmission submission);

    @Insert("insert into course_test_answer (submission_id, question_id, answer, score, is_correct, is_auto_graded) " +
            "values (#{submission_id}, #{question_id}, #{answer}, #{score}, #{is_correct}, #{is_auto_graded})")
    boolean addTestAnswerScored(@Param("submission_id") Long submission_id, @Param("question_id") Long question_id,
                                @Param("answer") String answer, @Param("score") Integer score,
                                @Param("is_correct") Integer is_correct, @Param("is_auto_graded") Integer is_auto_graded);

    @Select("select * from course_test_answer where submission_id = #{submission_id}")
    List<TestAnswer> getTestAnswersFullBySubmissionId(@Param("submission_id") Long submission_id);

    @Update("update course_test_answer set score = #{score} where submission_id = #{submission_id} and question_id = #{question_id}")
    boolean updateTestAnswerScore(@Param("submission_id") Long submission_id, @Param("question_id") Long question_id,
                                  @Param("score") Integer score);

    @Update("update course_test_submission set auto_score = #{auto_score}, manual_score = #{manual_score}, total_score = #{total_score}, is_fully_graded = #{is_fully_graded} where id = #{id}")
    boolean updateTestSubmissionScores(@Param("id") Long id, @Param("auto_score") Integer auto_score,
                                       @Param("manual_score") Integer manual_score,
                                       @Param("total_score") Integer total_score,
                                       @Param("is_fully_graded") Integer is_fully_graded);

    @Select("select s.*, a.name as account_name from course_test_submission s " +
            "left join accounts a on s.account = a.account " +
            "where s.activity_id = #{activity_id} order by s.submit_time desc")
    List<TestSubmission> getTestSubmissionsByActivityId(@Param("activity_id") Long activity_id);

    @Select("select * from course_test_submission where id = #{id} limit 1")
    TestSubmission getTestSubmissionById(@Param("id") Long id);
}
