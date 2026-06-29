package org.example.classpiserver.dto.prep;

public class PublishPrepRequest {
    private Long prep_id;
    private Integer class_id;
    private String teacher_account;
    private String deadline;
    private String start_time;
    private String homework_type;

    public Long getPrep_id() { return prep_id; }
    public void setPrep_id(Long prep_id) { this.prep_id = prep_id; }
    public Integer getClass_id() { return class_id; }
    public void setClass_id(Integer class_id) { this.class_id = class_id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }
    public String getHomework_type() { return homework_type; }
    public void setHomework_type(String homework_type) { this.homework_type = homework_type; }
}
