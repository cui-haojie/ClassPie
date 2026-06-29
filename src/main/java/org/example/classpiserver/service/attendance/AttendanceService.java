package org.example.classpiserver.service.attendance;

import org.example.classpiserver.dto.attendance.AttendanceDetailDTO;
import org.example.classpiserver.dto.attendance.CheckInRequest;
import org.example.classpiserver.dto.attendance.StartAttendanceRequest;
import org.example.classpiserver.entity.AttendanceRecord;
import org.example.classpiserver.entity.AttendanceSession;

import java.util.List;

public interface AttendanceService {
    AttendanceSession startAttendance(StartAttendanceRequest request);
    AttendanceDetailDTO getAttendanceDetail(Long sessionId, String account);
    AttendanceSession getOpenSession(Long classId);
    List<AttendanceSession> listSessions(Long classId);
    boolean checkIn(CheckInRequest request);
    boolean closeAttendance(Long sessionId, String teacherAccount);
}
