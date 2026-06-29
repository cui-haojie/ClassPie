package org.example.classpiserver.mapper.schoolclass;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.SchoolClass;

import java.util.List;

@Mapper
public interface SchoolClassMapper {

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

    @Select("select sc.* from student_class st join school_class sc on st.school_class_id = sc.id where st.account = #{account} limit 1")
    SchoolClass getSchoolClassByStudentAccount(@Param("account") String account);

    @Select("select sc.* from student_class st join school_class sc on st.school_class_id = sc.id where st.account = #{account} order by sc.id")
    List<SchoolClass> getSchoolClassesByStudentAccount(@Param("account") String account);
}
