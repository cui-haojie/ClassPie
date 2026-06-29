package org.example.classpiserver.service.attendance;

import org.example.classpiserver.dto.attendance.AttendanceDetailDTO;
import org.example.classpiserver.dto.attendance.AttendanceMemberDTO;
import org.example.classpiserver.dto.attendance.CheckInRequest;
import org.example.classpiserver.dto.attendance.StartAttendanceRequest;
import org.example.classpiserver.entity.AttendanceRecord;
import org.example.classpiserver.entity.AttendanceSession;
import org.example.classpiserver.entity.Course;
import org.example.classpiserver.entity.CourseMember;
import org.example.classpiserver.live.LiveEventPublisher;
import org.example.classpiserver.mapper.account.AccountMapper;
import org.example.classpiserver.mapper.attendance.AttendanceMapper;
import org.example.classpiserver.mapper.course.CourseMapper;
import org.example.classpiserver.service.notification.NotificationService;
import org.example.classpiserver.util.HomeworkDeadlineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private LiveEventPublisher liveEventPublisher;

    @Autowired
    private NotificationService notificationService;

    @Override
    public AttendanceSession startAttendance(StartAttendanceRequest request) {
        if (request == null || request.getClass_id() == null || request.getTeacher_account() == null) {
            return null;
        }
        if (!canTeachCourse(request.getTeacher_account(), request.getClass_id())) {
            return null;
        }
        AttendanceSession open = attendanceMapper.getOpenSessionByClassId(request.getClass_id());
        if (open != null) {
            attendanceMapper.closeSession(open.getId());
        }
        AttendanceSession session = new AttendanceSession();
        session.setClass_id(request.getClass_id());
        session.setTeacher_account(request.getTeacher_account());
        session.setCode(generateCode());
        session.setStatus("open");
        session.setDuration_minutes(request.getDuration_minutes() == null ? 5 : request.getDuration_minutes());
        if (!attendanceMapper.insertSession(session)) {
            return null;
        }
        normalizeSession(session);
        liveEventPublisher.publishCourse(request.getClass_id(), "attendance_started", session);
        notificationService.notifyAttendanceStarted(request.getClass_id().intValue(), session.getId());
        return session;
    }

    @Override
    public AttendanceDetailDTO getAttendanceDetail(Long sessionId, String account) {
        if (sessionId == null) {
            return null;
        }
        AttendanceSession session = attendanceMapper.getSessionById(sessionId);
        if (session == null) {
            return null;
        }
        normalizeSession(session);
        List<AttendanceRecord> records = attendanceMapper.listRecordsBySession(sessionId);
        for (AttendanceRecord record : records) {
            if (record.getCheck_time() != null) {
                record.setCheck_time(HomeworkDeadlineUtil.formatDisplay(record.getCheck_time()));
            }
        }
        List<CourseMember> students = listExpectedStudents(session.getClass_id());
        Set<String> checkedAccounts = new HashSet<>();
        for (AttendanceRecord record : records) {
            if (record.getAccount() != null) {
                checkedAccounts.add(record.getAccount());
            }
        }
        int expectedCount = students == null ? 0 : students.size();
        int checkedCount = records.size();
        session.setStudent_count(expectedCount);
        session.setChecked_count(checkedCount);

        boolean isTeacher = account != null && canTeachCourse(account, session.getClass_id());
        AttendanceDetailDTO dto = new AttendanceDetailDTO();
        dto.setSession(session);
        dto.setIs_teacher(isTeacher);
        dto.setExpected_count(expectedCount);
        dto.setChecked_count(checkedCount);
        dto.setChecked_in(false);
        if (account != null) {
            dto.setChecked_in(checkedAccounts.contains(account));
        }
        dto.setRecords(records);
        if (isTeacher && students != null) {
            List<AttendanceMemberDTO> absentMembers = new ArrayList<>();
            for (CourseMember student : students) {
                if (student.getAccount() == null || checkedAccounts.contains(student.getAccount())) {
                    continue;
                }
                absentMembers.add(new AttendanceMemberDTO(
                        student.getAccount(),
                        student.getName(),
                        student.getStatus_number()
                ));
            }
            dto.setAbsent_members(absentMembers);
        }
        return dto;
    }

    @Override
    public AttendanceSession getOpenSession(Long classId) {
        AttendanceSession session = attendanceMapper.getOpenSessionByClassId(classId);
        normalizeSession(session);
        return session;
    }

    @Override
    public List<AttendanceSession> listSessions(Long classId) {
        List<AttendanceSession> sessions = attendanceMapper.listSessionsByClassId(classId);
        for (AttendanceSession session : sessions) {
            normalizeSession(session);
        }
        return sessions;
    }

    @Override
    public boolean checkIn(CheckInRequest request) {
        if (request == null || request.getSession_id() == null || request.getAccount() == null) {
            return false;
        }
        if ("老师".equals(accountMapper.getAccountStatus(request.getAccount()))) {
            return false;
        }
        AttendanceSession session = attendanceMapper.getSessionById(request.getSession_id());
        if (session == null || !"open".equals(session.getStatus())) {
            return false;
        }
        if (request.getCode() == null || !request.getCode().trim().equals(session.getCode())) {
            return false;
        }
        Integer existing = attendanceMapper.countRecord(request.getSession_id(), request.getAccount());
        if (existing != null && existing > 0) {
            return true;
        }
        AttendanceRecord record = new AttendanceRecord();
        record.setSession_id(request.getSession_id());
        record.setAccount(request.getAccount());
        record.setStatus("present");
        boolean ok = attendanceMapper.insertRecord(record);
        if (ok) {
            liveEventPublisher.publishAttendance(request.getSession_id(), "attendance_checked_in",
                    java.util.Map.of("account", request.getAccount()));
            liveEventPublisher.publishCourse(session.getClass_id(), "attendance_updated",
                    java.util.Map.of("session_id", session.getId()));
        }
        return ok;
    }

    @Override
    public boolean closeAttendance(Long sessionId, String teacherAccount) {
        AttendanceSession session = attendanceMapper.getSessionById(sessionId);
        if (session == null || !teacherAccount.equals(session.getTeacher_account())) {
            return false;
        }
        boolean ok = attendanceMapper.closeSession(sessionId);
        if (ok) {
            liveEventPublisher.publishCourse(session.getClass_id(), "attendance_closed",
                    java.util.Map.of("session_id", sessionId));
        }
        return ok;
    }

    private void normalizeSession(AttendanceSession session) {
        if (session == null) {
            return;
        }
        if (session.getCreate_time() != null) {
            session.setCreate_time(HomeworkDeadlineUtil.formatDisplay(session.getCreate_time()));
        }
        if (session.getClose_time() != null) {
            session.setClose_time(HomeworkDeadlineUtil.formatDisplay(session.getClose_time()));
        }
    }

    private String generateCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    private List<CourseMember> listExpectedStudents(Long classId) {
        if (classId == null) {
            return List.of();
        }
        List<CourseMember> members = courseMapper.getCourseMembers(classId);
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        Course course = courseMapper.getCourseByCourseId(classId);
        String ownerAccount = course == null ? null : course.getTeacher_account();
        List<CourseMember> students = new ArrayList<>();
        for (CourseMember member : members) {
            if (member.getAccount() == null) {
                continue;
            }
            if (member.getAccount().equals(ownerAccount)) {
                continue;
            }
            if (isTeacherMember(member)) {
                continue;
            }
            students.add(member);
        }
        return students;
    }

    private boolean isTeacherMember(CourseMember member) {
        String status = member.getStatus();
        if (status != null && !status.isBlank()) {
            return "老师".equals(status);
        }
        return "老师".equals(accountMapper.getAccountStatus(member.getAccount()));
    }

    private boolean canTeachCourse(String teacherAccount, Long classId) {
        if (teacherAccount == null || classId == null) {
            return false;
        }
        if (!"老师".equals(accountMapper.getAccountStatus(teacherAccount))) {
            return false;
        }
        Course course = courseMapper.getCourseByCourseId(classId);
        if (course == null) {
            return false;
        }
        if (teacherAccount.equals(course.getTeacher_account())) {
            return true;
        }
        Integer inCourse = courseMapper.countAccountInCourse(teacherAccount, classId);
        return inCourse != null && inCourse > 0;
    }
}
