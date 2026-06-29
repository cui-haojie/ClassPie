package org.example.classpiserver.mapper.attendance;

import org.apache.ibatis.annotations.*;
import org.example.classpiserver.entity.AttendanceRecord;
import org.example.classpiserver.entity.AttendanceSession;

import java.util.List;

@Mapper
public interface AttendanceMapper {

    @Insert("insert into course_attendance_session (class_id, teacher_account, code, status, duration_minutes) " +
            "values (#{class_id}, #{teacher_account}, #{code}, #{status}, #{duration_minutes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean insertSession(AttendanceSession session);

    @Select("select * from course_attendance_session where id = #{id}")
    AttendanceSession getSessionById(@Param("id") Long id);

    @Select("select * from course_attendance_session where class_id = #{class_id} and status = 'open' order by id desc limit 1")
    AttendanceSession getOpenSessionByClassId(@Param("class_id") Long class_id);

    @Select("select s.*, " +
            "(select count(*) from course_attendance_record r where r.session_id = s.id) as checked_count, " +
            "(select count(*) from account_course ac where ac.class_id = s.class_id and ac.is_archived = 0 " +
            "and ac.account <> (select c.teacher_account from courses c where c.id = s.class_id)) as student_count " +
            "from course_attendance_session s where s.class_id = #{class_id} order by s.id desc limit 20")
    List<AttendanceSession> listSessionsByClassId(@Param("class_id") Long class_id);

    @Update("update course_attendance_session set status = 'closed', close_time = NOW() where id = #{id}")
    boolean closeSession(@Param("id") Long id);

    @Insert("insert into course_attendance_record (session_id, account, status) values (#{session_id}, #{account}, #{status})")
    boolean insertRecord(AttendanceRecord record);

    @Select("select count(*) from course_attendance_record where session_id = #{session_id} and account = #{account}")
    Integer countRecord(@Param("session_id") Long session_id, @Param("account") String account);

    @Select("select r.*, a.name as account_name from course_attendance_record r " +
            "left join accounts a on r.account = a.account where r.session_id = #{session_id} order by r.check_time asc")
    List<AttendanceRecord> listRecordsBySession(@Param("session_id") Long session_id);

    @Select("select count(distinct session_id) from course_attendance_record r " +
            "inner join course_attendance_session s on r.session_id = s.id " +
            "where s.class_id = #{class_id} and r.account = #{account}")
    Integer countStudentAttendanceSessions(@Param("class_id") Long class_id, @Param("account") String account);

    @Select("select count(*) from course_attendance_session where class_id = #{class_id}")
    Integer countSessionsByClassId(@Param("class_id") Long class_id);
}
