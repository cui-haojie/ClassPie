package org.example.classpiserver.mapper.grade;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.CourseGradeWeight;

@Mapper
public interface GradeWeightMapper {

    @Select("select * from course_grade_weight where course_id = #{course_id}")
    CourseGradeWeight getByCourseId(@Param("course_id") Long course_id);

    @Insert("insert into course_grade_weight (course_id, homework_weight, test_weight, attendance_weight, interaction_weight) " +
            "values (#{course_id}, #{homework_weight}, #{test_weight}, #{attendance_weight}, #{interaction_weight}) " +
            "on duplicate key update homework_weight = values(homework_weight), test_weight = values(test_weight), " +
            "attendance_weight = values(attendance_weight), interaction_weight = values(interaction_weight)")
    boolean upsert(CourseGradeWeight weight);
}
