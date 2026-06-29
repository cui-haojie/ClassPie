package org.example.classpiserver.controller.attendance;

import org.example.classpiserver.dto.attendance.*;
import org.example.classpiserver.entity.AttendanceSession;
import org.example.classpiserver.service.attendance.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/startAttendance")
    public AttendanceSession startAttendance(@RequestBody StartAttendanceRequest request) {
        return attendanceService.startAttendance(request);
    }

    @PostMapping("/getAttendanceDetail")
    public AttendanceDetailDTO getAttendanceDetail(@RequestBody SessionIdRequest request) {
        return attendanceService.getAttendanceDetail(request.getSession_id(), request.getAccount());
    }

    @PostMapping("/getOpenAttendance")
    public AttendanceSession getOpenAttendance(@RequestBody ClassIdAccountRequest request) {
        return attendanceService.getOpenSession(request.getClass_id());
    }

    @PostMapping("/listAttendances")
    public List<AttendanceSession> listAttendances(@RequestBody ClassIdAccountRequest request) {
        return attendanceService.listSessions(request.getClass_id());
    }

    @PostMapping("/checkInAttendance")
    public boolean checkInAttendance(@RequestBody CheckInRequest request) {
        return attendanceService.checkIn(request);
    }

    @PostMapping("/closeAttendance")
    public boolean closeAttendance(@RequestBody SessionIdRequest request) {
        return attendanceService.closeAttendance(request.getSession_id(), request.getTeacher_account());
    }
}
