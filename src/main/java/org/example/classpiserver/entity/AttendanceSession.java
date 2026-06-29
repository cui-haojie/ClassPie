package org.example.classpiserver.entity;

public class AttendanceSession {
    private Long id;
    private Long class_id;
    private String teacher_account;
    private String code;
    private String status;
    private Integer duration_minutes;
    private String create_time;
    private String close_time;
    private Integer checked_count;
    private Integer student_count;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClass_id() { return class_id; }
    public void setClass_id(Long class_id) { this.class_id = class_id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getDuration_minutes() { return duration_minutes; }
    public void setDuration_minutes(Integer duration_minutes) { this.duration_minutes = duration_minutes; }
    public String getCreate_time() { return create_time; }
    public void setCreate_time(String create_time) { this.create_time = create_time; }
    public String getClose_time() { return close_time; }
    public void setClose_time(String close_time) { this.close_time = close_time; }
    public Integer getChecked_count() { return checked_count; }
    public void setChecked_count(Integer checked_count) { this.checked_count = checked_count; }
    public Integer getStudent_count() { return student_count; }
    public void setStudent_count(Integer student_count) { this.student_count = student_count; }
}
