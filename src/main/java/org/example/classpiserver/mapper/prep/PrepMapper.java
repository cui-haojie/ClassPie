package org.example.classpiserver.mapper.prep;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.TeacherPrepItem;
import org.example.classpiserver.entity.TeacherPrepTestQuestion;

import java.util.List;

@Mapper
public interface PrepMapper {

    @Select("select * from teacher_prep_item where teacher_account = #{teacher_account} " +
            "and (#{kind} is null or #{kind} = '' or kind = #{kind}) order by update_time desc, id desc")
    List<TeacherPrepItem> listByTeacher(@Param("teacher_account") String teacher_account, @Param("kind") String kind);

    @Select("select * from teacher_prep_item where id = #{id}")
    TeacherPrepItem getById(@Param("id") Long id);

    @Insert("insert into teacher_prep_item (teacher_account, kind, title, content, attachment_url, attachment_name, meta_json) " +
            "values (#{teacher_account}, #{kind}, #{title}, #{content}, #{attachment_url}, #{attachment_name}, #{meta_json})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean insertItem(TeacherPrepItem item);

    @Update("update teacher_prep_item set kind = #{kind}, title = #{title}, content = #{content}, " +
            "attachment_url = #{attachment_url}, attachment_name = #{attachment_name}, meta_json = #{meta_json} where id = #{id}")
    boolean updateItem(TeacherPrepItem item);

    @Delete("delete from teacher_prep_item where id = #{id}")
    boolean deleteItem(@Param("id") Long id);

    @Select("select * from teacher_prep_test_question where prep_item_id = #{prep_item_id} order by sort_order asc, id asc")
    List<TeacherPrepTestQuestion> listQuestions(@Param("prep_item_id") Long prep_item_id);

    @Delete("delete from teacher_prep_test_question where prep_item_id = #{prep_item_id}")
    boolean deleteQuestions(@Param("prep_item_id") Long prep_item_id);

    @Insert("insert into teacher_prep_test_question (prep_item_id, question_type, stem, option_a, option_b, option_c, option_d, correct_option, score, sort_order) " +
            "values (#{prep_item_id}, #{question_type}, #{stem}, #{option_a}, #{option_b}, #{option_c}, #{option_d}, #{correct_option}, #{score}, #{sort_order})")
    boolean insertQuestion(TeacherPrepTestQuestion question);
}
