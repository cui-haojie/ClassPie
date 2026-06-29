package org.example.classpiserver.mapper.grade;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GradeMapper {

    @Select("SELECT COALESCE(AVG(c.score), 0) FROM content c " +
            "INNER JOIN courses_homework ch ON c.content_id = ch.homework_id " +
            "WHERE ch.class_id = #{class_id} AND c.account = #{account} AND c.is_graded = 1")
    Double avgHomeworkScore(@Param("class_id") Long class_id, @Param("account") String account);

    @Select("SELECT COALESCE(AVG(CASE WHEN s.max_score > 0 THEN s.total_score * 100.0 / s.max_score ELSE 0 END), 0) " +
            "FROM course_test_submission s " +
            "INNER JOIN course_activity ca ON s.activity_id = ca.id " +
            "WHERE ca.class_id = #{class_id} AND s.account = #{account} AND s.is_fully_graded = 1")
    Double avgTestScorePercent(@Param("class_id") Long class_id, @Param("account") String account);

    @Select("SELECT COUNT(DISTINCT r.activity_id) FROM course_interaction_response r " +
            "INNER JOIN course_activity ca ON r.activity_id = ca.id " +
            "WHERE ca.class_id = #{class_id} AND r.account = #{account}")
    Integer countInteractionParticipation(@Param("class_id") Long class_id, @Param("account") String account);
}
