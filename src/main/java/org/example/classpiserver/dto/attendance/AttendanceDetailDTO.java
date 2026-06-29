package org.example.classpiserver.dto.attendance;

import org.example.classpiserver.entity.AttendanceRecord;
import org.example.classpiserver.entity.AttendanceSession;

import java.util.List;

public class AttendanceDetailDTO {
    private AttendanceSession session;
    private Boolean is_teacher;
    private Boolean checked_in;
    private Integer expected_count;
    private Integer checked_count;
    private List<AttendanceRecord> records;
    private List<AttendanceMemberDTO> absent_members;

    public AttendanceSession getSession() { return session; }
    public void setSession(AttendanceSession session) { this.session = session; }
    public Boolean getIs_teacher() { return is_teacher; }
    public void setIs_teacher(Boolean is_teacher) { this.is_teacher = is_teacher; }
    public Boolean getChecked_in() { return checked_in; }
    public void setChecked_in(Boolean checked_in) { this.checked_in = checked_in; }
    public Integer getExpected_count() { return expected_count; }
    public void setExpected_count(Integer expected_count) { this.expected_count = expected_count; }
    public Integer getChecked_count() { return checked_count; }
    public void setChecked_count(Integer checked_count) { this.checked_count = checked_count; }
    public List<AttendanceRecord> getRecords() { return records; }
    public void setRecords(List<AttendanceRecord> records) { this.records = records; }
    public List<AttendanceMemberDTO> getAbsent_members() { return absent_members; }
    public void setAbsent_members(List<AttendanceMemberDTO> absent_members) { this.absent_members = absent_members; }
}
