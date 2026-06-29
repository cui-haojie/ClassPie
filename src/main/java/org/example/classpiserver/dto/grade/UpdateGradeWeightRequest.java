package org.example.classpiserver.dto.grade;

public class UpdateGradeWeightRequest {
    private Long class_id;
    private String teacher_account;
    private Integer homework_weight;
    private Integer test_weight;
    private Integer attendance_weight;
    private Integer interaction_weight;

    public Long getClass_id() { return class_id; }
    public void setClass_id(Long class_id) { this.class_id = class_id; }
    public String getTeacher_account() { return teacher_account; }
    public void setTeacher_account(String teacher_account) { this.teacher_account = teacher_account; }
    public Integer getHomework_weight() { return homework_weight; }
    public void setHomework_weight(Integer homework_weight) { this.homework_weight = homework_weight; }
    public Integer getTest_weight() { return test_weight; }
    public void setTest_weight(Integer test_weight) { this.test_weight = test_weight; }
    public Integer getAttendance_weight() { return attendance_weight; }
    public void setAttendance_weight(Integer attendance_weight) { this.attendance_weight = attendance_weight; }
    public Integer getInteraction_weight() { return interaction_weight; }
    public void setInteraction_weight(Integer interaction_weight) { this.interaction_weight = interaction_weight; }
}
