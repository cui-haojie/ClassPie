package org.example.classpiserver.dto.attendance;

public class StartAttendanceRequest {
    private Long class_id;
    private String teacher_account;
    private Integer duration_minutes;

    public Long getClass_id() { return class_id; }
    public void setClass_id(Long class_id) { this.class_id = class_id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public Integer getDuration_minutes() { return duration_minutes; }
    public void setDuration_minutes(Integer duration_minutes) { this.duration_minutes = duration_minutes; }
}
